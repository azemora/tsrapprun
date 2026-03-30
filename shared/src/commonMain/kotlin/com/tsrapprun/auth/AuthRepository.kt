/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  AuthRepository.kt - Interface de Autenticação              ║
 * ║                                                             ║
 * ║  Define o contrato de autenticação que cada plataforma      ║
 * ║  deve implementar. Usa expect/actual do KMP.                ║
 * ║                                                             ║
 * ║  PADRÃO REPOSITORY:                                         ║
 * ║  Abstrai a fonte de dados de autenticação (Firebase,        ║
 * ║  Apple Sign-In, etc.) da lógica de negócio e da UI.         ║
 * ║  Isso permite:                                              ║
 * ║  1. Trocar o provedor de auth sem mudar a UI                ║
 * ║  2. Testar a UI com mocks                                   ║
 * ║  3. Manter a mesma interface em Android e iOS               ║
 * ║                                                             ║
 * ║  SEGURANÇA:                                                 ║
 * ║  - signOut() limpa TODOS os dados de sessão                 ║
 * ║  - Erros são capturados e nunca propagam exceções raw       ║
 * ║  - Flow emite estados, nunca dados sensíveis                ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.auth

import kotlinx.coroutines.flow.StateFlow

/**
 * Repositório de autenticação multiplataforma.
 *
 * Cada plataforma implementa esta classe com seu SDK nativo:
 * - Android: Firebase Auth + Credential Manager
 * - iOS: Firebase Auth + Apple Sign-In / Google Sign-In
 *
 * IMPORTANTE: Todas as operações são suspending functions
 * porque envolvem I/O (rede, Keystore, Keychain).
 * Nunca bloqueiam a Main Thread.
 */
expect class AuthRepository {
    /**
     * Flow reativo com o estado atual da autenticação.
     *
     * A UI observa este flow e reage automaticamente:
     * - Loading → mostra spinner
     * - Unauthenticated → mostra tela de login
     * - Authenticated → mostra tela principal
     * - Error → mostra mensagem de erro
     *
     * É um StateFlow (não SharedFlow) porque:
     * 1. Sempre tem um valor atual (sem perder estados)
     * 2. Novos observers recebem o estado atual imediatamente
     * 3. Não emite valores duplicados consecutivos
     */
    val authState: StateFlow<AuthState>

    /**
     * Inicia o fluxo de login com Google.
     *
     * FLUXO COMPLETO:
     * 1. Abre a UI de seleção de conta Google (Credential Manager)
     * 2. Usuário seleciona/confirma a conta
     * 3. Recebe o Google ID Token
     * 4. Troca o Google ID Token por um Firebase ID Token
     * 5. Salva o Firebase ID Token no TokenManager (criptografado)
     * 6. Emite AuthState.Authenticated com os dados do usuário
     *
     * SEGURANÇA:
     * - O Google ID Token tem vida curta (~1 hora)
     * - O Firebase gerencia a renovação automática
     * - Em caso de erro, emite AuthState.Error com mensagem genérica
     *
     * @throws Nunca — erros são capturados e emitidos como AuthState.Error
     */
    suspend fun signInWithGoogle()

    /**
     * Faz logout completo e limpa todos os dados de sessão.
     *
     * OPERAÇÕES REALIZADAS:
     * 1. Firebase Auth sign out (invalida sessão no servidor)
     * 2. TokenManager.clearAllTokens() (destrói tokens locais)
     * 3. Emite AuthState.Unauthenticated
     *
     * SEGURANÇA: Após esta operação, não há como restaurar
     * a sessão sem novo login. Tokens locais são destruídos
     * e a sessão do Firebase é invalidada no servidor.
     */
    suspend fun signOut()

    /**
     * Verifica se existe uma sessão válida ao abrir o app.
     *
     * Chamado no início do app para restaurar sessão sem
     * pedir login novamente (se o token ainda for válido).
     *
     * FLUXO:
     * 1. Verifica se há token no TokenManager
     * 2. Se sim, verifica validade com Firebase Auth
     * 3. Se válido → AuthState.Authenticated
     * 4. Se inválido → AuthState.Unauthenticated (login necessário)
     */
    suspend fun checkCurrentSession()
}
