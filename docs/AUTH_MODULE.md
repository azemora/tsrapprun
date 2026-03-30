# Módulo de Autenticação — tsrapprun

## Visão Geral

O módulo de autenticação implementa **Google Sign-In** usando **Firebase Auth** com
**Credential Manager** (API moderna do Android que substitui o GoogleSignInClient descontinuado).

Toda a arquitetura segue o princípio **Security by Design** com foco em:
- Minimização de dados (LGPD Art. 6°, GDPR Art. 5(1)(c))
- Criptografia em repouso e em trânsito
- Princípio do menor privilégio
- Zero trust (tokens nunca em texto plano)

---

## Arquitetura

```
┌─────────────────────────────────────────────────────┐
│                    UI (Compose)                      │
│  LoginScreen.kt ←→ App.kt ←→ HomeScreen.kt         │
└──────────────┬──────────────────────┬───────────────┘
               │ observa StateFlow    │ chama
               ▼                      ▼
┌─────────────────────────────────────────────────────┐
│              AuthRepository (expect/actual)           │
│  - signInWithGoogle()                                │
│  - signOut()                                         │
│  - checkCurrentSession()                             │
│  - authState: StateFlow<AuthState>                   │
└──────────────┬──────────────────────┬───────────────┘
               │                      │
               ▼                      ▼
┌──────────────────────┐  ┌───────────────────────────┐
│   Firebase Auth       │  │   TokenManager             │
│   (Google Sign-In)    │  │   (expect/actual)          │
│                       │  │                            │
│   - Valida JWT        │  │   Android: Keystore +      │
│   - Gerencia sessão   │  │     EncryptedSharedPrefs   │
│   - Renova tokens     │  │   iOS: Keychain Services   │
└───────────────────────┘  └───────────────────────────┘
```

---

## Estrutura de Arquivos

```
shared/src/
├── commonMain/kotlin/com/tsrapprun/
│   ├── App.kt                          # Composable raiz (navegação por estado)
│   ├── Platform.kt                     # expect fun getPlatformName()
│   ├── auth/
│   │   ├── AuthState.kt               # Sealed class com estados de autenticação
│   │   ├── AuthRepository.kt          # Interface expect do repositório
│   │   ├── LoginScreen.kt             # Tela de login (Compose)
│   │   └── HomeScreen.kt              # Tela principal pós-login (Compose)
│   └── security/
│       ├── UserData.kt                # Modelo de dados do usuário (imutável)
│       └── TokenManager.kt            # Interface expect de gerenciamento de tokens
│
├── androidMain/kotlin/com/tsrapprun/
│   ├── Platform.android.kt            # actual fun getPlatformName()
│   ├── auth/
│   │   └── AuthRepository.android.kt  # Implementação Android (Firebase + Credential Manager)
│   └── security/
│       └── TokenManager.android.kt    # Implementação Android (Keystore + EncryptedSharedPrefs)
│
└── iosMain/kotlin/com/tsrapprun/
    ├── Platform.ios.kt                 # actual fun getPlatformName()
    ├── MainViewController.kt           # Entry point iOS
    ├── auth/
    │   └── AuthRepository.ios.kt       # Stub iOS (a implementar com Xcode)
    └── security/
        └── TokenManager.ios.kt         # Implementação iOS (Keychain Services)

androidApp/src/main/java/com/tsrapprun/
└── MainActivity.kt                     # Activity Android (inicializa AuthRepository)
```

---

## Fluxo de Autenticação

### Login (signInWithGoogle)

```
1. Usuário toca "Entrar com Google"
   │
2. Credential Manager abre bottom sheet de seleção de conta
   │ (não há auto-select — segurança)
   │
3. Usuário seleciona conta Google
   │
4. Google retorna ID Token (JWT assinado, expira em ~1h)
   │
5. Firebase Auth valida o JWT com o Google
   │ (comunicação servidor-servidor, não passa pelo app)
   │
6. Firebase cria sessão e retorna Firebase ID Token
   │
7. Firebase ID Token é criptografado (AES-256-GCM)
   │ e salvo no Android Keystore via TokenManager
   │
8. AuthState muda para Authenticated
   │ (UI reage automaticamente via StateFlow)
   │
9. Tela de login → Tela principal (HomeScreen)
```

### Logout (signOut)

```
1. Usuário toca "Sair da conta"
   │
2. Firebase Auth sign out (invalida sessão no servidor)
   │
3. TokenManager.clearAllTokens()
   │ (destrói tokens criptografados do Keystore)
   │
4. AuthState muda para Unauthenticated
   │
5. Tela principal → Tela de login
```

### Restauração de Sessão (checkCurrentSession)

```
1. App abre (onCreate)
   │
2. Verifica Firebase Auth currentUser
   │
   ├── Existe → verifica token com Firebase
   │   ├── Válido → AuthState.Authenticated
   │   └── Inválido → limpa tokens → AuthState.Unauthenticated
   │
   └── Não existe → AuthState.Unauthenticated
```

---

## Segurança

### Criptografia de Tokens (Android)

| Camada | Algoritmo | Detalhes |
|--------|-----------|----------|
| Nomes de chaves | AES-256-SIV | Determinístico, sem IV |
| Valores | AES-256-GCM | Autenticado, com IV aleatório |
| Master Key | AES-256-GCM | Gerada no Android Keystore (TEE) |

### Criptografia de Tokens (iOS)

| Camada | Mecanismo | Detalhes |
|--------|-----------|----------|
| Armazenamento | iOS Keychain | Protegido pelo Secure Enclave |
| Acesso | kSecAttrAccessibleWhenUnlockedThisDeviceOnly | Só com dispositivo desbloqueado |
| Backup | Não incluído | Dados não vão para iCloud/iTunes |

### Princípios Aplicados

1. **Minimização de dados**: UserData contém apenas userId, displayName, email, photoUrl
2. **Tokens nunca em texto plano**: Android Keystore (TEE) / iOS Keychain (Secure Enclave)
3. **Mensagens de erro genéricas**: Nunca expõem detalhes internos (OWASP A01:2021)
4. **allowBackup="false"**: Impede backup de dados sensíveis via ADB
5. **usesCleartextTraffic="false"**: Força HTTPS em todas as comunicações
6. **Logout completo**: Destrói tokens locais + invalida sessão no servidor
7. **Sem auto-select**: Usuário sempre confirma a conta (previne uso indevido)

---

## Dependências

| Biblioteca | Versão | Propósito |
|-----------|--------|-----------|
| Firebase Auth KTX | 23.1.0 | Autenticação com Google |
| Firebase BoM | 33.7.0 | Gerenciamento de versões (androidApp) |
| Credential Manager | 1.3.0 | API moderna de credenciais |
| Google ID | 1.1.1 | Gerar requests de Google Sign-In |
| Security Crypto | 1.1.0-alpha06 | EncryptedSharedPreferences |
| Kotlinx Serialization | 1.7.3 | Serialização segura de dados |

---

## Configuração do Firebase

### Passo a passo para ativar Google Sign-In:

1. **Criar projeto no Firebase Console** (https://console.firebase.google.com)
   - Nome: `tsrapprun`
   - Ativar Google Analytics (opcional)

2. **Registrar app Android**
   - Package name: `com.tsrapprun.android`
   - Gerar SHA-1 do debug keystore:
     ```bash
     keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
     ```

3. **Baixar google-services.json**
   - Salvar em `androidApp/google-services.json`
   - **NUNCA commitar** (está no .gitignore)

4. **Ativar Google Sign-In no Firebase**
   - Firebase Console → Authentication → Sign-in method → Google → Enable

5. **Copiar o Web Client ID**
   - Firebase Console → Authentication → Sign-in method → Google
   - Copiar "Web client ID"
   - Colar em `MainActivity.kt` no campo `webClientId`

---

## Controle de Custos

### Firebase Auth (Grátis)
- **Spark Plan (grátis)**: Autenticação ilimitada com Google
- Sem custo por login/logout
- Sem custo por verificação de sessão

### Limites do Spark Plan
- 10k verificações de telefone/mês (não usamos)
- Storage de usuários: ilimitado
- Custo: **$0.00**

---

## Próximos Passos

1. [ ] Configurar projeto Firebase real
2. [ ] Substituir `google-services.json` placeholder pelo real
3. [ ] Substituir `YOUR_WEB_CLIENT_ID` pelo ID real em `MainActivity.kt`
4. [ ] Implementar AuthRepository para iOS (requer Mac + Xcode)
5. [ ] Adicionar Firebase App Check (proteção contra uso abusivo)
6. [ ] Implementar módulo de câmera (próxima fase)
