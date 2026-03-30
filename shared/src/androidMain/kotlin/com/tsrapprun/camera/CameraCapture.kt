/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  CameraCapture.kt - Composable de Câmera Android            ║
 * ║                                                             ║
 * ║  Implementa a captura de fotos usando CameraX.              ║
 * ║  CameraX é a API moderna do Android para câmera:            ║
 * ║  - Lifecycle-aware (inicia/para automaticamente)            ║
 * ║  - Compatível com 90%+ dos dispositivos Android             ║
 * ║  - Gerenciamento automático de recursos                     ║
 * ║                                                             ║
 * ║  SEGURANÇA:                                                 ║
 * ║  - Fotos são capturadas em memória (ByteArray)              ║
 * ║  - NUNCA salvam em disco sem criptografia                   ║
 * ║  - São passadas diretamente ao LocalPhotoStorage            ║
 * ║    que criptografa antes de persistir                       ║
 * ║  - Nenhum arquivo temporário não-criptografado é criado     ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.nio.ByteBuffer

/**
 * Composable que exibe o preview da câmera e botão de captura.
 *
 * FLUXO:
 * 1. Exibe preview em tempo real da câmera traseira
 * 2. Usuário toca no botão de captura
 * 3. CameraX captura a foto em memória (ImageProxy → ByteArray)
 * 4. ByteArray é passado ao callback onPhotoCaptured
 * 5. O caller (HomeScreen) envia ao LocalPhotoStorage para criptografar
 *
 * SEGURANÇA: A foto existe apenas em memória entre a captura e
 * a criptografia. Nenhum arquivo temporário é criado.
 *
 * @param onPhotoCaptured Callback com os bytes da foto capturada.
 *                        O caller é responsável por criptografar e salvar.
 * @param onError Callback de erro com mensagem amigável.
 */
@Composable
fun CameraPreview(
    onPhotoCaptured: (ByteArray) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Estado: indica se está capturando (evita múltiplos cliques)
    var isCapturing by remember { mutableStateOf(false) }

    // Referência ao ImageCapture — usado para tirar a foto
    val imageCapture = remember {
        ImageCapture.Builder()
            // QUALITY_BALANCED: boa qualidade sem usar muito espaço
            // Alternativas: CAPTURE_MODE_MAXIMIZE_QUALITY (mais pesado)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ── Preview da Câmera (ocupa tela toda) ──
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).also { previewView ->
                    // Inicia o CameraX vinculado ao lifecycle da Activity
                    startCamera(ctx, previewView, imageCapture, lifecycleOwner)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // ── Botão de Captura (círculo branco no centro inferior) ──
        Button(
            onClick = {
                if (!isCapturing) {
                    isCapturing = true
                    capturePhoto(
                        imageCapture = imageCapture,
                        context = context,
                        onSuccess = { bytes ->
                            isCapturing = false
                            onPhotoCaptured(bytes)
                        },
                        onError = { error ->
                            isCapturing = false
                            onError(error)
                        }
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(72.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ),
            enabled = !isCapturing
        ) {
            if (isCapturing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

/**
 * Inicia o CameraX e vincula ao lifecycle.
 *
 * SEGURANÇA: CameraX é lifecycle-aware — a câmera é
 * liberada automaticamente quando a Activity/Fragment é destruída.
 * Isso evita vazamento de recursos e acesso indevido à câmera.
 */
private fun startCamera(
    context: Context,
    previewView: PreviewView,
    imageCapture: ImageCapture,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        // Configura o preview (o que o usuário vê na tela)
        val preview = Preview.Builder()
            .build()
            .also {
                it.surfaceProvider = previewView.surfaceProvider
            }

        // Usa câmera traseira por padrão (melhor qualidade)
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            // Remove bindings anteriores (evita conflitos)
            cameraProvider.unbindAll()

            // Vincula preview + capture ao lifecycle
            // Quando a Activity é destruída, CameraX libera tudo
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )

            Log.d("CameraCapture", "Câmera iniciada com sucesso")
        } catch (e: Exception) {
            Log.e("CameraCapture", "Erro ao iniciar câmera", e)
        }
    }, ContextCompat.getMainExecutor(context))
}

/**
 * Captura uma foto e retorna os bytes em memória.
 *
 * FLUXO DE SEGURANÇA:
 * 1. CameraX captura via ImageCapture.takePicture()
 * 2. Resultado chega como ImageProxy (em memória)
 * 3. Convertemos ImageProxy → ByteArray
 * 4. ImageProxy é fechado (libera memória nativa)
 * 5. ByteArray é passado ao callback (para criptografia)
 *
 * NENHUM ARQUIVO TEMPORÁRIO É CRIADO.
 * A foto vai direto da câmera → memória → criptografia → disco.
 */
private fun capturePhoto(
    imageCapture: ImageCapture,
    context: Context,
    onSuccess: (ByteArray) -> Unit,
    onError: (String) -> Unit
) {
    imageCapture.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                try {
                    // Converte ImageProxy → ByteArray (JPEG)
                    val buffer: ByteBuffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)

                    Log.d("CameraCapture", "Foto capturada: ${bytes.size} bytes")
                    onSuccess(bytes)
                } catch (e: Exception) {
                    Log.e("CameraCapture", "Erro ao processar imagem", e)
                    onError("Erro ao processar a foto. Tente novamente.")
                } finally {
                    // IMPORTANTE: Sempre fechar o ImageProxy para liberar
                    // o buffer nativo da câmera
                    image.close()
                }
            }

            override fun onError(exception: ImageCaptureException) {
                // SEGURANÇA: Não expõe detalhes técnicos ao usuário
                Log.e("CameraCapture", "Erro na captura", exception)
                onError("Não foi possível tirar a foto. Tente novamente.")
            }
        }
    )
}
