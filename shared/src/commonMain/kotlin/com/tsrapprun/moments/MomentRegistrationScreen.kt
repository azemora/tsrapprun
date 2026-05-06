/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MomentRegistrationScreen.kt                                 ║
 * ║                                                              ║
 * ║  Tradução de fullapp/screens/moment-registration.jsx +       ║
 * ║  funcionalidades:                                            ║
 * ║   • Tap na polaroide → escolha câmera ou galeria             ║
 * ║   • Tag "RASCUNHO" no canto: salva draft manualmente         ║
 * ║   • Voltar com conteúdo: oferece salvar como rascunho        ║
 * ║   • Auto-load do rascunho ao abrir                           ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.moments

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.platform.dateComponentsOf
import com.tsrapprun.platform.nowMillis
import com.tsrapprun.ui.chrome.OliveDeep
import com.tsrapprun.ui.chrome.PrimaryButton
import com.tsrapprun.ui.chrome.ScreenHeader
import com.tsrapprun.ui.chrome.Tag
import com.tsrapprun.ui.chrome.italicSerifText
import com.tsrapprun.ui.theme.CozyAmber
import com.tsrapprun.ui.theme.CozyAmberDeep
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozyCreamDeep
import com.tsrapprun.ui.theme.CozyGold
import com.tsrapprun.ui.theme.CozyOlive
import kotlinx.coroutines.launch

@Composable
fun MomentRegistrationScreen(
    initialDraft: MomentDraft? = null,
    initialPhotoIds: List<String> = emptyList(),
    initialPhotos: List<com.tsrapprun.camera.PhotoData> = emptyList(),
    initialTitle: String? = null,
    initialNote: String? = null,
    initialIsMilestone: Boolean = false,
    onSave: (title: String, note: String, photoIds: List<String>, isMilestone: Boolean) -> Unit,
    onCancel: () -> Unit,
    onSaveDraft: suspend (MomentDraft) -> Unit = {},
    onClearDraft: suspend () -> Unit = {},
    onPickFromCamera: () -> Unit = {},
    onPickFromGallery: () -> Unit = {},
    onLoadPhoto: suspend (com.tsrapprun.camera.PhotoData) -> ByteArray? = { null }
) {
    // Prioridade: initialTitle (prefill explícito de outra tela) > rascunho salvo.
    var title by remember { mutableStateOf(initialTitle ?: initialDraft?.title ?: "") }
    var note by remember { mutableStateOf(initialNote ?: initialDraft?.note ?: "") }
    var isMilestone by remember { mutableStateOf(initialIsMilestone) }

    var showPhotoPicker by remember { mutableStateOf(false) }
    var showBackDialog by remember { mutableStateOf(false) }
    var showDraftSavedToast by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val today = remember {
        val c = dateComponentsOf(nowMillis())
        val short = listOf("jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez")
        "hoje, ${c.day} ${short[c.monthIndex]}"
    }

    val hasContent = title.isNotBlank() || note.isNotBlank() || initialPhotoIds.isNotEmpty()
    val photoCount = initialPhotoIds.size

    fun buildDraft() = MomentDraft(
        title = title,
        note = note,
        type = "DAILY",
        updatedAt = nowMillis()
    )

    fun handleBack() {
        if (hasContent) {
            showBackDialog = true
        } else {
            onCancel()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(CozyCream)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            ScreenHeader(
                chapter = "novo registro",
                title = italicSerifText(
                    prefix = "conte um ",
                    italic = "momento",
                    suffix = ".",
                    italicColor = CozyAmberDeep,
                    defaultColor = OliveDeep
                ),
                subtitle = "o que aconteceu hoje? não precisa ser perfeito.",
                onBack = { handleBack() },
                rightContent = {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (showDraftSavedToast) CozyAmberDeep else CozyCreamDeep,
                        modifier = Modifier.clickable(enabled = hasContent) {
                            scope.launch {
                                onSaveDraft(buildDraft())
                                showDraftSavedToast = true
                            }
                        }
                    ) {
                        Tag(
                            if (showDraftSavedToast) "rascunho salvo ✓" else "rascunho",
                            color = if (showDraftSavedToast) CozyCream else CozyOlive,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Foto(s): mostra strip de polaroides ou o card "tocar pra adicionar".
                if (photoCount > 0) {
                    PhotoStrip(
                        photos = initialPhotos,
                        photoCount = photoCount,
                        loader = onLoadPhoto,
                        onAdd = { showPhotoPicker = true }
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        PolaroidUploadCard(
                            onClick = { showPhotoPicker = true },
                            photoCount = photoCount
                        )
                    }
                }

                // Título
                Column {
                    Tag("título", color = OliveDeep)
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = CozyCreamDeep,
                        border = BorderStroke(1.4.dp, CozyOlive.copy(alpha = 0.15f))
                    ) {
                        BasicTextField(
                            value = title,
                            onValueChange = {
                                title = it
                                showDraftSavedToast = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            singleLine = true,
                            cursorBrush = SolidColor(CozyAmber),
                            textStyle = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = OliveDeep,
                                letterSpacing = (-0.3).sp
                            ),
                            decorationBox = { inner ->
                                if (title.isEmpty()) {
                                    Text(
                                        "ex: primeira papinha",
                                        fontFamily = FontFamily.Serif,
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 16.sp,
                                        color = CozyOlive.copy(alpha = 0.4f)
                                    )
                                }
                                inner()
                            }
                        )
                    }
                }

                // Notinha
                Column {
                    Tag("uma notinha (opcional)", color = OliveDeep)
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = CozyCreamDeep,
                        border = BorderStroke(1.4.dp, CozyOlive.copy(alpha = 0.15f))
                    ) {
                        BasicTextField(
                            value = note,
                            onValueChange = {
                                note = it
                                showDraftSavedToast = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            cursorBrush = SolidColor(CozyAmber),
                            textStyle = TextStyle(
                                fontFamily = FontFamily.Cursive,
                                fontSize = 19.sp,
                                color = OliveDeep,
                                lineHeight = 26.sp
                            ),
                            decorationBox = { inner ->
                                if (note.isEmpty()) {
                                    Text(
                                        "escreva o que sentiu...",
                                        fontFamily = FontFamily.Cursive,
                                        fontSize = 19.sp,
                                        color = CozyOlive.copy(alpha = 0.35f),
                                        lineHeight = 26.sp
                                    )
                                }
                                inner()
                            }
                        )
                    }
                }

                // Toggle "marcar como marco"
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isMilestone = !isMilestone },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isMilestone) CozyAmberDeep.copy(alpha = 0.12f) else CozyCreamDeep,
                    border = BorderStroke(
                        1.4.dp,
                        if (isMilestone) CozyAmberDeep else CozyOlive.copy(alpha = 0.15f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "✦",
                            fontSize = 18.sp,
                            color = if (isMilestone) CozyAmberDeep else CozyOlive.copy(alpha = 0.5f)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = italicSerifText(
                                    italic = "marcar como marco",
                                    italicColor = if (isMilestone) CozyAmberDeep else OliveDeep,
                                    defaultColor = OliveDeep,
                                    italicWeight = FontWeight.Bold
                                ),
                                fontFamily = FontFamily.Serif,
                                fontSize = 15.sp,
                                color = OliveDeep
                            )
                            Text(
                                "primeira vez · aniversário · momento especial",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = CozyOlive.copy(alpha = 0.6f),
                                letterSpacing = 0.5.sp
                            )
                        }
                        // "checkbox" visual
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(
                                    if (isMilestone) CozyAmberDeep else Color.Transparent
                                )
                                .border(
                                    1.5.dp,
                                    if (isMilestone) CozyAmberDeep else CozyOlive.copy(alpha = 0.4f),
                                    androidx.compose.foundation.shape.CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isMilestone) {
                                Text(
                                    "✓",
                                    color = CozyCream,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Data (info pill)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    InfoPill(
                        modifier = Modifier.weight(1f),
                        label = "data",
                        value = today
                    )
                    InfoPill(
                        modifier = Modifier.weight(1f),
                        label = "marcadores",
                        value = "+ adicionar",
                        italic = true
                    )
                }
            }

            // Footer CTA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CozyCream)
                    .border(BorderStroke(1.dp, CozyOlive.copy(alpha = 0.1f)), shape = RoundedCornerShape(0.dp))
                    .padding(horizontal = 24.dp, vertical = 14.dp)
            ) {
                PrimaryButton(
                    text = if (photoCount > 0) "guardar registro" else "guardar momento",
                    onClick = {
                        if (hasContent) {
                            scope.launch {
                                onClearDraft()
                                onSave(title.trim(), note.trim(), initialPhotoIds, isMilestone)
                            }
                        }
                    }
                )
            }
        }
    }

    // ── Diálogo: escolher câmera ou galeria ──
    if (showPhotoPicker) {
        AlertDialog(
            onDismissRequest = { showPhotoPicker = false },
            containerColor = CozyCream,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    italicSerifText(
                        prefix = "adicionar ",
                        italic = "foto",
                        italicColor = CozyAmberDeep,
                        defaultColor = OliveDeep
                    ),
                    fontSize = 22.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            showPhotoPicker = false
                            // Persiste rascunho antes de navegar pra câmera, pra restaurar
                            // o título/nota quando o usuário voltar.
                            if (hasContent) {
                                scope.launch { onSaveDraft(buildDraft()) }
                            }
                            onPickFromCamera()
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OliveDeep)
                    ) {
                        Text(
                            italicSerifText(
                                italic = "câmera",
                                suffix = " · tirar agora",
                                italicColor = CozyCream,
                                defaultColor = CozyCream,
                                defaultWeight = FontWeight.Medium,
                                italicWeight = FontWeight.Bold
                            ),
                            fontFamily = FontFamily.Serif,
                            fontSize = 14.sp
                        )
                    }
                    Button(
                        onClick = {
                            showPhotoPicker = false
                            if (hasContent) {
                                scope.launch { onSaveDraft(buildDraft()) }
                            }
                            onPickFromGallery()
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CozyAmberDeep)
                    ) {
                        Text(
                            italicSerifText(
                                italic = "galeria",
                                suffix = " · escolher uma",
                                italicColor = CozyCream,
                                defaultColor = CozyCream,
                                defaultWeight = FontWeight.Medium,
                                italicWeight = FontWeight.Bold
                            ),
                            fontFamily = FontFamily.Serif,
                            fontSize = 14.sp
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPhotoPicker = false }) {
                    Text("cancelar", color = CozyOlive)
                }
            }
        )
    }

    // ── Diálogo: voltar com conteúdo ──
    if (showBackDialog) {
        AlertDialog(
            onDismissRequest = { showBackDialog = false },
            containerColor = CozyCream,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    italicSerifText(
                        prefix = "salvar como ",
                        italic = "rascunho",
                        suffix = "?",
                        italicColor = CozyAmberDeep,
                        defaultColor = OliveDeep
                    ),
                    fontSize = 20.sp
                )
            },
            text = {
                Text(
                    "você tem alterações não salvas. quer guardar pra continuar depois?",
                    fontSize = 13.sp,
                    color = CozyOlive,
                    lineHeight = 19.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showBackDialog = false
                        scope.launch {
                            onSaveDraft(buildDraft())
                            onCancel()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OliveDeep)
                ) {
                    Text("salvar rascunho", color = CozyCream)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showBackDialog = false
                    scope.launch {
                        onClearDraft()
                        onCancel()
                    }
                }) {
                    Text("descartar", color = Color(0xFFB85450))
                }
            }
        )
    }
}

/** Polaroid mini que renderiza a foto real quando carregada. */
@Composable
private fun PolaroidWithPhoto(
    photo: com.tsrapprun.camera.PhotoData?,
    loader: suspend (com.tsrapprun.camera.PhotoData) -> ByteArray?,
    width: androidx.compose.ui.unit.Dp,
    photoHeight: androidx.compose.ui.unit.Dp,
    rotation: Float,
    caption: String? = null
) {
    val bitmap = com.tsrapprun.ui.photos.rememberPhotoBitmap(photo, loader)
    androidx.compose.material3.Surface(
        modifier = Modifier
            .width(width)
            .graphicsLayer { rotationZ = rotation },
        shape = RoundedCornerShape(4.dp),
        color = CozyCream,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp).padding(bottom = if (caption != null) 4.dp else 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(photoHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(com.tsrapprun.ui.chrome.PhotoGray)
            ) {
                if (bitmap != null) {
                    androidx.compose.foundation.Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
            }
            if (caption != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    caption,
                    fontFamily = FontFamily.Cursive,
                    fontSize = 14.sp,
                    color = OliveDeep,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PhotoStrip(
    photos: List<com.tsrapprun.camera.PhotoData>,
    photoCount: Int,
    loader: suspend (com.tsrapprun.camera.PhotoData) -> ByteArray?,
    onAdd: () -> Unit
) {
    val rotations = listOf(-5f, 4f, -3f, 6f, -2f)
    val showCount = photoCount.coerceAtMost(3)
    val extra = photoCount - showCount
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(showCount) { i ->
            val photo = photos.getOrNull(i)
            PolaroidWithPhoto(
                photo = photo,
                loader = loader,
                width = 84.dp,
                photoHeight = 76.dp,
                rotation = rotations[i % rotations.size],
                caption = if (i == showCount - 1 && extra > 0) "+ $extra" else null
            )
        }
        // Botão "+" pra adicionar mais — formato polaroid
        Surface(
            modifier = Modifier
                .width(84.dp)
                .height(110.dp)
                .clickable(onClick = onAdd)
                .graphicsLayer { rotationZ = 3f },
            shape = RoundedCornerShape(4.dp),
            color = CozyCream,
            border = BorderStroke(1.4.dp, CozyOlive.copy(alpha = 0.35f)),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(76.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(CozyCreamDeep),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "+",
                        fontFamily = FontFamily.Serif,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Light,
                        color = OliveDeep
                    )
                }
            }
        }
    }
}

@Composable
private fun PolaroidUploadCard(onClick: () -> Unit, photoCount: Int = 0) {
    Box(modifier = Modifier.size(width = 200.dp, height = 230.dp).clickable(onClick = onClick)) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationZ = -3f },
            shape = RoundedCornerShape(4.dp),
            color = CozyCream,
            border = BorderStroke(1.dp, CozyOlive.copy(alpha = 0.4f)),
            shadowElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (photoCount > 0) com.tsrapprun.ui.chrome.PhotoGray else CozyCreamDeep),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoCount == 0) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📷", fontSize = 32.sp, color = CozyAmberDeep)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "tocar para adicionar foto",
                                fontFamily = FontFamily.Serif,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = CozyAmberDeep
                            )
                        }
                    } else {
                        // Indicador de foto(s) já capturada(s)
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = CozyCream.copy(alpha = 0.92f)
                        ) {
                            Text(
                                if (photoCount == 1) "1 foto · tocar pra adicionar mais" else "$photoCount fotos · tocar pra adicionar mais",
                                fontFamily = FontFamily.Serif,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = OliveDeep,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "___________",
                    fontFamily = FontFamily.Cursive,
                    fontSize = 17.sp,
                    color = OliveDeep,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-10).dp)
                .graphicsLayer { rotationZ = -4f }
                .size(width = 80.dp, height = 22.dp)
                .background(CozyGold.copy(alpha = 0.55f))
        )
    }
}

@Composable
private fun InfoPill(modifier: Modifier, label: String, value: String, italic: Boolean = false) {
    Column(modifier = modifier) {
        Tag(label, color = OliveDeep)
        Spacer(Modifier.height(8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = CozyCreamDeep,
            border = BorderStroke(1.4.dp, CozyOlive.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    value,
                    fontFamily = FontFamily.Serif,
                    fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
                    fontSize = 14.sp,
                    color = OliveDeep
                )
                Text(
                    if (italic) "+" else "▤",
                    fontSize = 16.sp,
                    color = CozyOlive,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}
