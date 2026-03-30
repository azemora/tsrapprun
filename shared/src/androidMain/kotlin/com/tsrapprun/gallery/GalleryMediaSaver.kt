/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  GalleryMediaSaver.kt - Salva fotos no álbum público       ║
 * ║                                                             ║
 * ║  Usa MediaStore para criar um álbum "TSR App Run" visível  ║
 * ║  na galeria do Android. Fotos persistem mesmo se o app     ║
 * ║  for desinstalado.                                         ║
 * ║                                                             ║
 * ║  API 29+: MediaStore com RELATIVE_PATH (sem permissão)     ║
 * ║  API 24-28: MediaStore com DATA + WRITE_EXTERNAL_STORAGE   ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.gallery

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object GalleryMediaSaver {

    private const val TAG = "GalleryMediaSaver"
    private const val ALBUM_NAME = "TSR App Run"

    /**
     * Salva uma cópia da foto no álbum público "TSR App Run".
     * A foto fica visível na galeria do Android e persiste
     * independentemente da instalação do app.
     *
     * @param context Contexto Android
     * @param imageBytes Bytes JPEG da foto
     * @param displayName Nome do arquivo (sem extensão)
     * @return true se salvou com sucesso
     */
    suspend fun saveToGallery(
        context: Context,
        imageBytes: ByteArray,
        displayName: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // API 29+: usa RELATIVE_PATH para definir o álbum
                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        "${Environment.DIRECTORY_PICTURES}/$ALBUM_NAME"
                    )
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                } else {
                    // API 24-28: cria diretório manualmente e usa DATA
                    val albumDir = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        ALBUM_NAME
                    )
                    if (!albumDir.exists()) albumDir.mkdirs()
                    @Suppress("DEPRECATION")
                    put(
                        MediaStore.Images.Media.DATA,
                        File(albumDir, "$displayName.jpg").absolutePath
                    )
                }
            }

            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )

            if (uri == null) {
                Log.e(TAG, "Falha ao inserir no MediaStore")
                return@withContext false
            }

            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(imageBytes)
                out.flush()
            } ?: run {
                Log.e(TAG, "Falha ao abrir OutputStream para $uri")
                return@withContext false
            }

            // API 29+: marca como disponível (não mais pendente)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val updateValues = ContentValues().apply {
                    put(MediaStore.Images.Media.IS_PENDING, 0)
                }
                context.contentResolver.update(uri, updateValues, null, null)
            }

            Log.d(TAG, "Foto salva no álbum '$ALBUM_NAME': $displayName")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao salvar no álbum público", e)
            false
        }
    }
}
