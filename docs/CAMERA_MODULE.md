# Módulo de Câmera e Armazenamento Local — tsrapprun

## Visão Geral

O módulo de câmera captura fotos de eventos e armazena localmente
com criptografia AES-256-GCM. Nenhum dado binário sai do dispositivo
sem autorização explícita do usuário.

**Princípio: Local-First, Encrypted-Always**

---

## Arquitetura

```
┌──────────────────────────────────────────────────────┐
│                  UI (Compose)                         │
│  HomeScreen → CameraScreen → HomeScreen              │
│  (botão)      (preview+captura)  (galeria+stats)     │
└─────────┬────────────────────────────┬───────────────┘
          │ ByteArray (memória)        │ lista fotos
          ▼                            ▼
┌──────────────────┐    ┌──────────────────────────────┐
│  CameraX          │    │  LocalPhotoStorage            │
│  (captura)        │    │  (criptografia + persistência) │
│                   │    │                               │
│  Câmera→ImageProxy│    │  ByteArray → AES-256-GCM     │
│  →ByteArray       │    │  → arquivo .enc              │
│  (só memória)     │    │  + índice JSON criptografado  │
└──────────────────┘    └──────────────────────────────┘
                              │
                              ▼
                    ┌─────────────────────┐
                    │  Android Keystore    │
                    │  (MasterKey AES-256) │
                    │  Hardware-backed     │
                    └─────────────────────┘
```

---

## Fluxo de Captura (Seguro)

```
1. Usuário toca "Tirar Foto" no HomeScreen
   │
2. CameraScreen abre → verifica permissão CAMERA
   │ (solicitada em runtime, não no install)
   │
3. CameraX exibe preview da câmera traseira
   │
4. Usuário toca no botão de captura (círculo branco)
   │
5. CameraX captura → ImageProxy (memória nativa)
   │
6. ImageProxy → ByteArray (cópia em memória Kotlin)
   │ ImageProxy.close() libera buffer nativo
   │
7. ByteArray passado ao MainActivity via callback
   │ ⚠️ NENHUM ARQUIVO TEMPORÁRIO CRIADO ATÉ AQUI
   │
8. LocalPhotoStorage.savePhoto(bytes)
   │ a. Gera UUID + timestamp como nome do arquivo
   │ b. Cria EncryptedFile com MasterKey do Keystore
   │ c. Criptografa bytes com AES-256-GCM (streaming)
   │ d. Salva arquivo .enc no diretório privado do app
   │ e. Atualiza índice JSON (também criptografado)
   │
9. HomeScreen atualiza contadores (fotos + MB)
```

**Ponto chave**: A foto existe em texto plano APENAS em memória,
entre os passos 6 e 8c. Nunca toca o disco sem criptografia.

---

## Estrutura de Arquivos

```
shared/src/
├── commonMain/kotlin/com/tsrapprun/
│   ├── camera/
│   │   ├── PhotoData.kt          # Modelo de metadados da foto
│   │   └── CameraScreen.kt       # Interface expect da tela de câmera
│   └── storage/
│       └── LocalPhotoStorage.kt   # Interface expect do armazenamento
│
├── androidMain/kotlin/com/tsrapprun/
│   ├── camera/
│   │   ├── CameraCapture.kt      # Composable CameraX (preview + captura)
│   │   └── CameraScreen.android.kt # Implementação Android (permissão + câmera)
│   └── storage/
│       └── LocalPhotoStorage.android.kt # Armazenamento criptografado Android
│
└── iosMain/kotlin/com/tsrapprun/
    ├── camera/
    │   └── CameraScreen.ios.kt    # Stub iOS
    └── storage/
        └── LocalPhotoStorage.ios.kt # Stub iOS
```

---

## Criptografia

### Arquivos de Foto (.enc)

| Propriedade | Valor |
|-------------|-------|
| Algoritmo | AES-256-GCM-HKDF-4KB (Tink StreamingAead) |
| Chave | MasterKey no Android Keystore (TEE) |
| Streaming | Sim — criptografa em chunks de 4KB |
| Autenticação | GCM (detecta tampering automaticamente) |
| IV/Nonce | Único por chunk (gerado automaticamente) |

### Índice de Fotos (photo_index.json.enc)

| Propriedade | Valor |
|-------------|-------|
| Conteúdo | JSON com lista de PhotoData |
| Criptografia | Mesma do arquivo de foto (AES-256-GCM) |
| Thread-safety | Protegido por Mutex (coroutines) |

### Onde ficam os arquivos

```
/data/data/com.tsrapprun.android/files/
├── photos/
│   ├── uuid1_1234567890.enc    # Foto 1 (criptografada)
│   ├── uuid2_1234567891.enc    # Foto 2 (criptografada)
│   └── ...
└── photo_index.json.enc         # Índice (criptografado)
```

- Diretório `files/` é app-specific (sandbox Android)
- Inacessível por outros apps sem root
- `allowBackup="false"` → não incluído em backups ADB

---

## Permissões

| Permissão | Tipo | Quando | Motivo |
|-----------|------|--------|--------|
| CAMERA | Perigosa (runtime) | Ao abrir câmera | Capturar fotos |
| INTERNET | Normal (install) | Sempre | Firebase Auth |
| ACCESS_NETWORK_STATE | Normal (install) | Sempre | Verificar conexão |

- `uses-feature camera required="false"` → app funciona sem câmera
- Permissão CAMERA solicitada via `ActivityResultContracts` (API moderna)

---

## Controle de Armazenamento

| Métrica | Como acessar |
|---------|-------------|
| Total de fotos | `LocalPhotoStorage.listPhotos().size` |
| Espaço usado (bytes) | `LocalPhotoStorage.getTotalStorageUsed()` |
| Fotos por evento | `LocalPhotoStorage.listPhotosByEvent(eventId)` |

O HomeScreen exibe essas métricas em tempo real.

---

## Próximos Passos

1. [ ] Galeria de fotos com grid (exibir thumbnails decifradas)
2. [ ] Deletar foto individual (swipe-to-delete)
3. [ ] Associar fotos a eventos
4. [ ] Upload opcional para Storage (quando ativado)
5. [ ] Implementar câmera iOS (AVFoundation)
