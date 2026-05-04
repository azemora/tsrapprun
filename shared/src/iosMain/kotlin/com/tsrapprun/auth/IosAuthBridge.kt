package com.tsrapprun.auth

import com.tsrapprun.security.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class IosAuthBridge {

    private val _authState: MutableStateFlow<AuthState> =
        MutableStateFlow(AuthState.Loading)

    val authState: StateFlow<AuthState> get() = _authState

    var onSignInClick: () -> Unit = {}
    var onSignOutClick: () -> Unit = {}

    fun setAuthenticated(
        userId: String,
        displayName: String?,
        email: String?,
        photoUrl: String?
    ) {
        _authState.value = AuthState.Authenticated(
            UserData(
                userId = userId,
                displayName = displayName,
                email = email,
                photoUrl = photoUrl
            )
        )
    }

    fun setUnauthenticated() {
        _authState.value = AuthState.Unauthenticated
    }

    fun setError(message: String) {
        _authState.value = AuthState.Error(message)
    }

    fun setLoading() {
        _authState.value = AuthState.Loading
    }
}
