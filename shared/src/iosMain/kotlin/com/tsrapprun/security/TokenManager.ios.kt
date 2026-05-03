package com.tsrapprun.security

import platform.Foundation.NSUserDefaults

// TODO: Substituir por iOS Keychain (Secure Enclave) antes de produção.
// NSUserDefaults é protegido pelo Data Protection do iOS, mas o Keychain
// oferece proteção mais forte. A versão anterior tentava interop com
// CoreFoundation/Security mas estava incompleta — esta é uma versão
// funcional para compilar e validar o pipeline. Veja:
// https://developer.apple.com/documentation/security/keychain_services
actual class TokenManager {

    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults

    actual suspend fun saveIdToken(token: String) {
        defaults.setObject(token, forKey = KEY_ID_TOKEN)
        defaults.synchronize()
    }

    actual suspend fun getIdToken(): String? {
        return defaults.stringForKey(KEY_ID_TOKEN)
    }

    actual suspend fun clearAllTokens() {
        defaults.removeObjectForKey(KEY_ID_TOKEN)
        defaults.synchronize()
    }

    actual suspend fun hasValidSession(): Boolean {
        return defaults.stringForKey(KEY_ID_TOKEN) != null
    }

    private companion object {
        const val KEY_ID_TOKEN = "com.tsrapprun.tokenmanager.id_token"
    }
}
