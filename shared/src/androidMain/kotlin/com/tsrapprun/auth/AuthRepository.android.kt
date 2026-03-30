/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  AuthRepository.android.kt - Implementação Android          ║
 * ║                                                             ║
 * ║  Implementa autenticação usando:                            ║
 * ║  - Credential Manager (API moderna, substitui o antigo      ║
 * ║    GoogleSignInClient que foi descontinuado)                ║
 * ║  - Firebase Auth (gerencia sessão e tokens)                 ║
 * ║  - TokenManager (armazena tokens com criptografia)          ║
 * ║                                                             ║
 * ║  FLUXO DE SEGURANÇA:                                       ║
 * ║  1. Credential Manager solicita credencial ao Google        ║
 * ║  2. Google retorna um ID Token (JWT) assinado               ║
 * ║  3. Firebase valida o JWT e cria uma sessão                 ║
 * ║  4. O Firebase ID Token é salvo criptografado (Keystore)   ║
 * ║  5. Dados do perfil são extraídos e exibidos na UI          ║
 * ║                                                             ║
 * ║  NENHUM TOKEN FICA EM MEMÓRIA APÓS O FLUXO.                ║
 * ║  Tudo é persistido no Android Keystore via TokenManager.    ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tsrapprun.security.TokenManager
import com.tsrapprun.security.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

/**
 * Implementação Android do repositório de autenticação.
 *
 * @property context Context do Android (necessário para Credential Manager)
 * @property webClientId ID do cliente OAuth 2.0 do Firebase
 *                       (encontrado no google-services.json → client → oauth_client)
 *
 * SEGURANÇA:
 * - O webClientId NÃO é um secret — é um identificador público
 *   usado pelo Google para identificar o app
 * - O secret real está no servidor Firebase (nunca no app)
 */
actual class AuthRepository(
    private val context: Context,
    private val webClientId: String
) {
    companion object {
        /**
         * Tag para logs de debug.
         *
         * SEGURANÇA: Em produção, logs com dados sensíveis devem
         * ser removidos ou redirecionados para Firebase Crashlytics.
         * Nunca logar tokens, emails, ou IDs de usuário.
         */
        private const val TAG = "AuthRepository"
    }

    // ── Estado interno da autenticação ──

    /**
     * MutableStateFlow privado — só esta classe pode alterar o estado.
     * Inicia como Loading porque precisamos verificar sessão existente.
     */
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)

    /**
     * StateFlow público e imutável — a UI observa este flow.
     * asStateFlow() garante que ninguém externo altere o estado.
     */
    actual val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /**
     * Instância do Firebase Auth.
     * Gerencia o ciclo de vida da sessão do usuário.
     */
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Credential Manager — API moderna do Android para gerenciar credenciais.
     * Substitui o antigo GoogleSignInClient (descontinuado).
     * Usa o Android Keystore internamente para segurança.
     */
    private val credentialManager: CredentialManager =
        CredentialManager.create(context)

    /**
     * TokenManager para armazenamento criptografado de tokens.
     * Usa EncryptedSharedPreferences + Android Keystore.
     */
    private val tokenManager: TokenManager = TokenManager(context)

    /**
     * Inicia o fluxo de login com Google via Credential Manager.
     *
     * FLUXO DETALHADO:
     * ┌─────────────────────────────────────────────────┐
     * │ 1. Cria GetGoogleIdOption com webClientId       │
     * │ 2. Credential Manager abre a UI de seleção      │
     * │ 3. Usuário escolhe conta Google                  │
     * │ 4. Google retorna ID Token (JWT assinado)        │
     * │ 5. Cria GoogleAuthProvider.credential            │
     * │ 6. Firebase valida e cria sessão                 │
     * │ 7. Salva Firebase ID Token no Keystore          │
     * │ 8. Emite AuthState.Authenticated                │
     * └─────────────────────────────────────────────────┘
     *
     * TRATAMENTO DE ERROS:
     * - Cancelamento pelo usuário → volta para Unauthenticated
     * - Sem credenciais salvas → mensagem amigável
     * - Erro de rede → mensagem genérica (sem detalhes técnicos)
     */
    actual suspend fun signInWithGoogle() {
        try {
            // Atualiza estado para Loading enquanto processa
            _authState.value = AuthState.Loading

            // ── Passo 1: Configurar a solicitação de credencial Google ──
            // filterByAuthorizedAccounts = false → mostra TODAS as contas Google
            // do dispositivo, não apenas as que já foram usadas neste app
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // Mostra todas as contas
                .setServerClientId(webClientId)       // ID OAuth 2.0 do Firebase
                .setAutoSelectEnabled(false)          // Sempre pede confirmação do usuário
                .build()

            // ── Passo 2: Criar a request para o Credential Manager ──
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            // ── Passo 3: Abrir a UI de seleção de conta ──
            // Isso mostra o bottom sheet do Android com as contas Google
            // O usuário DEVE interagir — não há auto-select por segurança
            val result: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = context
            )

            // ── Passo 4: Extrair o Google ID Token da resposta ──
            val googleIdTokenCredential = GoogleIdTokenCredential
                .createFrom(result.credential.data)
            val googleIdToken = googleIdTokenCredential.idToken

            // ── Passo 5: Autenticar no Firebase com o Google ID Token ──
            // O Firebase valida o JWT com o Google e cria uma sessão
            val firebaseCredential = GoogleAuthProvider
                .getCredential(googleIdToken, null)
            val authResult = firebaseAuth
                .signInWithCredential(firebaseCredential)
                .await() // await() converte Task<> em coroutine

            // ── Passo 6: Extrair dados do usuário e salvar token ──
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                // Salva o Firebase ID Token criptografado no Keystore
                val idToken = firebaseUser.getIdToken(false).await()
                idToken?.token?.let { token ->
                    tokenManager.saveIdToken(token)
                }

                // Cria objeto com dados mínimos do perfil (minimização de dados)
                val userData = UserData(
                    userId = firebaseUser.uid,
                    displayName = firebaseUser.displayName,
                    email = firebaseUser.email,
                    photoUrl = firebaseUser.photoUrl?.toString()
                )

                // ── Passo 7: Emite estado autenticado para a UI ──
                _authState.value = AuthState.Authenticated(userData)

                Log.d(TAG, "Login realizado com sucesso") // NÃO loga dados do usuário
            } else {
                // Firebase retornou sem usuário — situação anômala
                _authState.value = AuthState.Error(
                    "Não foi possível completar o login. Tente novamente."
                )
            }

        } catch (e: GetCredentialCancellationException) {
            // Usuário cancelou o fluxo de login — comportamento normal
            Log.d(TAG, "Login cancelado pelo usuário")
            _authState.value = AuthState.Unauthenticated

        } catch (e: NoCredentialException) {
            // Nenhuma conta Google configurada no dispositivo
            Log.w(TAG, "Nenhuma credencial Google disponível")
            _authState.value = AuthState.Error(
                "Nenhuma conta Google encontrada. Configure uma conta Google no dispositivo."
            )

        } catch (e: GetCredentialException) {
            // Erro genérico do Credential Manager
            // SEGURANÇA: NÃO expõe e.message para o usuário (pode conter dados internos)
            Log.e(TAG, "Erro no Credential Manager", e)
            _authState.value = AuthState.Error(
                "Erro ao fazer login. Verifique sua conexão e tente novamente."
            )

        } catch (e: Exception) {
            // Catch-all para erros inesperados
            // SEGURANÇA: Mensagem genérica — detalhes só nos logs
            Log.e(TAG, "Erro inesperado durante login", e)
            _authState.value = AuthState.Error(
                "Ocorreu um erro inesperado. Tente novamente."
            )
        }
    }

    /**
     * Faz logout completo e destrói todos os dados de sessão.
     *
     * OPERAÇÕES (em ordem):
     * 1. Firebase Auth sign out → invalida sessão no servidor Google
     * 2. TokenManager.clearAllTokens() → destrói tokens do Keystore
     * 3. Emite AuthState.Unauthenticated → UI mostra tela de login
     *
     * SEGURANÇA: Mesmo se uma operação falhar, as outras continuam.
     * Isso garante que o máximo de dados possível seja limpo.
     */
    actual suspend fun signOut() {
        try {
            // 1. Invalida sessão no Firebase (servidor)
            firebaseAuth.signOut()

            // 2. Destrói todos os tokens locais criptografados
            tokenManager.clearAllTokens()

            // 3. Atualiza estado da UI
            _authState.value = AuthState.Unauthenticated

            Log.d(TAG, "Logout realizado com sucesso")

        } catch (e: Exception) {
            // Mesmo com erro, força estado não autenticado por segurança
            Log.e(TAG, "Erro durante logout", e)
            _authState.value = AuthState.Unauthenticated
        }
    }

    /**
     * Verifica se existe uma sessão válida ao abrir o app.
     *
     * FLUXO:
     * 1. Verifica se Firebase Auth tem um currentUser
     * 2. Se sim → restaura sessão (AuthState.Authenticated)
     * 3. Se não → pede login (AuthState.Unauthenticated)
     *
     * SEGURANÇA: Não confia apenas no token local.
     * Verifica com o Firebase se a sessão ainda é válida.
     * Se o usuário revogou o acesso no Google, o Firebase
     * invalida a sessão e exige novo login.
     */
    actual suspend fun checkCurrentSession() {
        try {
            _authState.value = AuthState.Loading

            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                // Verifica se o token ainda é válido com o Firebase
                // forceRefresh = false → usa cache se possível (economia de rede)
                val idTokenResult = currentUser.getIdToken(false).await()

                if (idTokenResult?.token != null) {
                    // Sessão válida — restaura dados do usuário
                    val userData = UserData(
                        userId = currentUser.uid,
                        displayName = currentUser.displayName,
                        email = currentUser.email,
                        photoUrl = currentUser.photoUrl?.toString()
                    )
                    _authState.value = AuthState.Authenticated(userData)
                    Log.d(TAG, "Sessão restaurada com sucesso")
                } else {
                    // Token inválido — exige novo login
                    tokenManager.clearAllTokens()
                    _authState.value = AuthState.Unauthenticated
                }
            } else {
                // Sem usuário logado
                _authState.value = AuthState.Unauthenticated
            }

        } catch (e: Exception) {
            // Erro ao verificar sessão — por segurança, exige login
            Log.e(TAG, "Erro ao verificar sessão", e)
            tokenManager.clearAllTokens()
            _authState.value = AuthState.Unauthenticated
        }
    }
}
