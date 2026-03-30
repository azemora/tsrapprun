/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  AuthState.kt - Estados de Autenticação                     ║
 * ║                                                             ║
 * ║  Define os possíveis estados do fluxo de autenticação.      ║
 * ║  Usa sealed class para garantir que todos os estados         ║
 * ║  sejam tratados em tempo de compilação (exhaustive when).   ║
 * ║                                                             ║
 * ║  SEGURANÇA:                                                 ║
 * ║  - Erros NUNCA expõem detalhes internos ao usuário          ║
 * ║  - Mensagens de erro são genéricas para evitar               ║
 * ║    information disclosure (OWASP A01:2021)                  ║
 * ║  - Dados do usuário são imutáveis (data class UserData)     ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.auth

import com.tsrapprun.security.UserData

/**
 * Sealed class que representa todos os estados possíveis
 * do fluxo de autenticação no app.
 *
 * Sealed = o compilador Kotlin garante que TODO switch/when
 * trate todos os estados. Se adicionarmos um novo estado,
 * o código não compila até que seja tratado em toda a UI.
 *
 * Fluxo de estados:
 * ```
 * Loading → Unauthenticated ←→ Authenticated
 *                ↓
 *             Error → Unauthenticated
 * ```
 */
sealed class AuthState {
    /**
     * Estado inicial — verificando se existe sessão salva.
     * A UI deve mostrar um indicador de carregamento (spinner).
     */
    data object Loading : AuthState()

    /**
     * Usuário não está autenticado.
     * A UI deve mostrar a tela de login com botão "Sign in with Google".
     */
    data object Unauthenticated : AuthState()

    /**
     * Usuário autenticado com sucesso.
     *
     * @property userData Dados do perfil do usuário (nome, email, foto).
     *                    NOTA: Não contém tokens — esses estão no TokenManager.
     */
    data class Authenticated(val userData: UserData) : AuthState()

    /**
     * Erro durante autenticação.
     *
     * @property message Mensagem amigável para o usuário.
     *                   SEGURANÇA: Nunca contém stack traces, IDs internos,
     *                   ou informações que possam ajudar um atacante.
     *                   Detalhes técnicos são logados separadamente
     *                   (em produção, via Firebase Crashlytics).
     */
    data class Error(val message: String) : AuthState()
}
