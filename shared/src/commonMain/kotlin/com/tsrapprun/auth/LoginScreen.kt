/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  LoginScreen.kt - Tela de Login com Google                  ║
 * ║                                                             ║
 * ║  Tela compartilhada entre Android e iOS usando Compose      ║
 * ║  Multiplatform. Mesma UI em ambas as plataformas.           ║
 * ║                                                             ║
 * ║  DESIGN:                                                    ║
 * ║  - Botão "Entrar com Google" seguindo guidelines oficiais   ║
 * ║  - Indicador de carregamento durante autenticação           ║
 * ║  - Mensagem de erro com opção de tentar novamente           ║
 * ║  - Informação sobre proteção de dados (transparência)       ║
 * ║                                                             ║
 * ║  SEGURANÇA:                                                 ║
 * ║  - Nenhum dado sensível é exibido na tela                   ║
 * ║  - Mensagens de erro são genéricas                          ║
 * ║  - Transparência sobre uso de dados (LGPD/GDPR)            ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Tela de login principal.
 *
 * Exibe o botão de login com Google e informações sobre
 * proteção de dados. Reage ao [AuthState] para mostrar
 * loading, erro ou o botão de login.
 *
 * @param authState Estado atual da autenticação.
 * @param onSignInClick Callback chamado quando o usuário toca
 *                      no botão "Entrar com Google".
 *                      A Activity/ViewController chama
 *                      AuthRepository.signInWithGoogle().
 */
@Composable
fun LoginScreen(
    authState: AuthState,
    onSignInClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Logo / Título do App ──
            Text(
                text = "TSR App Run",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Fotos de eventos, seguras e organizadas",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ── Conteúdo dinâmico baseado no estado ──
            when (authState) {
                // Estado: Carregando (verificando sessão ou fazendo login)
                is AuthState.Loading -> {
                    LoadingContent()
                }

                // Estado: Não autenticado (mostra botão de login)
                is AuthState.Unauthenticated -> {
                    GoogleSignInButton(onClick = onSignInClick)
                }

                // Estado: Erro (mostra mensagem + botão de tentar novamente)
                is AuthState.Error -> {
                    ErrorContent(
                        message = authState.message,
                        onRetryClick = onSignInClick
                    )
                }

                // Estado: Autenticado — esta tela não deveria ser visível
                // (o App.kt navega para a tela principal)
                is AuthState.Authenticated -> {
                    // Não renderiza nada — a navegação cuidará disso
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // ── Informação sobre proteção de dados (transparência LGPD/GDPR) ──
            DataProtectionNotice()
        }
    }
}

/**
 * Indicador de carregamento com mensagem.
 * Exibido enquanto o login está sendo processado.
 */
@Composable
private fun LoadingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Verificando credenciais...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Botão "Entrar com Google" seguindo as guidelines do Google.
 *
 * DESIGN: Botão outlined com borda cinza, texto escuro,
 * seguindo o Material Design e Google Branding Guidelines.
 *
 * @param onClick Callback quando o botão é pressionado.
 */
@Composable
private fun GoogleSignInButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Ícone "G" do Google (texto estilizado como placeholder)
            // Em produção, substituir por ícone SVG oficial do Google
            Text(
                text = "G",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4285F4) // Azul Google oficial
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Entrar com Google",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F1F1F) // Texto escuro
            )
        }
    }
}

/**
 * Conteúdo exibido quando ocorre um erro de autenticação.
 *
 * SEGURANÇA: A mensagem exibida é genérica (definida em AuthState.Error).
 * Nunca exibe stack traces, códigos internos ou informações
 * que possam ajudar um atacante.
 *
 * @param message Mensagem de erro amigável para o usuário.
 * @param onRetryClick Callback para tentar login novamente.
 */
@Composable
private fun ErrorContent(
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mensagem de erro
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Botão "Tentar novamente"
        Button(
            onClick = onRetryClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                text = "Tentar novamente",
                fontSize = 16.sp
            )
        }
    }
}

/**
 * Aviso de proteção de dados exibido na tela de login.
 *
 * LGPD/GDPR: Transparência sobre como os dados são usados.
 * O usuário deve saber, ANTES de fazer login:
 * - Que dados são coletados
 * - Para que são usados
 * - Que ficam seguros (criptografados)
 */
@Composable
private fun DataProtectionNotice() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Seus dados estão protegidos",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Suas fotos são criptografadas e armazenadas localmente. " +
                    "O envio para a nuvem é opcional e requer sua autorização explícita. " +
                    "Usamos apenas seu nome e email para identificação.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}
