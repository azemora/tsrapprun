package com.tsrapprun

import androidx.compose.ui.window.ComposeUIViewController
import com.tsrapprun.auth.AuthState
import kotlinx.coroutines.flow.MutableStateFlow

fun MainViewController() = ComposeUIViewController {
    val authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    App(
        authState = authState,
        callbacks = AppCallbacks(),
        uiState = AppUiState()
    )
}
