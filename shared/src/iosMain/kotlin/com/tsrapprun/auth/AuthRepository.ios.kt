/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  AuthRepository.ios.kt - Implementação iOS (Stub)           ║
 * ║                                                             ║
 * ║  Implementação placeholder para iOS.                        ║
 * ║  Será completada quando houver acesso a um Mac com Xcode.   ║
 * ║                                                             ║
 * ║  A implementação final usará:                               ║
 * ║  - Firebase Auth iOS SDK                                    ║
 * ║  - Google Sign-In iOS SDK ou Apple Sign-In                  ║
 * ║  - iOS Keychain via TokenManager.ios.kt                     ║
 * ║                                                             ║
 * ║  NOTA: A interface é idêntica à do Android, garantindo      ║
 * ║  que a UI compartilhada funcione sem modificações.          ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.auth

import com.tsrapprun.security.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementação iOS do repositório de autenticação.
 *
 * TODO: Implementar com Firebase Auth iOS SDK quando
 * houver acesso a ambiente macOS com Xcode.
 */
actual class AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    actual val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /**
     * Stub — será implementado com Google Sign-In iOS SDK.
     */
    actual suspend fun signInWithGoogle() {
        _authState.value = AuthState.Error(
            "Login com Google ainda não disponível para iOS. Em breve!"
        )
    }

    /**
     * Stub — será implementado com Firebase Auth iOS.
     */
    actual suspend fun signOut() {
        _authState.value = AuthState.Unauthenticated
    }

    /**
     * Stub — será implementado com Firebase Auth iOS.
     */
    actual suspend fun checkCurrentSession() {
        _authState.value = AuthState.Unauthenticated
    }
}
