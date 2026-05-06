/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  HomeScreen.kt — tradução de fullapp/screens/home.jsx        ║
 * ║                                                              ║
 * ║  Top sage com saudação + nome editorial + dias de vida.      ║
 * ║  Body cream: card destaque (próximo mesversário) + últimos   ║
 * ║  registros (2 polaroides) + atalhos coloridos.               ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.auth

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.camera.EventData
import com.tsrapprun.child.AgeCalculator
import com.tsrapprun.child.ChildProfile
import com.tsrapprun.child.LifePhase
import com.tsrapprun.moments.MomentEntry
import com.tsrapprun.moments.MomentType
import com.tsrapprun.platform.dateComponentsOf
import com.tsrapprun.platform.nowMillis
import com.tsrapprun.security.UserData
import com.tsrapprun.ui.chrome.Butter
import com.tsrapprun.ui.chrome.MomentCard
import com.tsrapprun.ui.chrome.MomentKind
import com.tsrapprun.ui.chrome.OliveDeep
import com.tsrapprun.ui.chrome.Pastels
import com.tsrapprun.ui.chrome.Peach
import com.tsrapprun.ui.chrome.Polaroid
import com.tsrapprun.ui.chrome.Stamp
import com.tsrapprun.ui.chrome.Tag
import com.tsrapprun.ui.chrome.italicSerifText
import com.tsrapprun.ui.theme.CozyAmberDeep
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozyCreamDeep
import com.tsrapprun.ui.theme.CozyOlive
import com.tsrapprun.ui.theme.CozySage
import com.tsrapprun.ui.theme.CozySageMist
import kotlin.math.sin

@Composable
fun HomeScreen(
    userData: UserData,
    childProfile: ChildProfile? = null,
    photoCount: Int = 0,
    storageUsedMB: String = "0.0",
    events: List<EventData> = emptyList(),
    moments: List<MomentEntry> = emptyList(),
    allPhotos: List<com.tsrapprun.camera.PhotoData> = emptyList(),
    onLoadPhoto: suspend (com.tsrapprun.camera.PhotoData) -> ByteArray? = { null },
    onRegisterEvent: () -> Unit = {},
    onQuickRegister: () -> Unit = {},
    onAddReminder: () -> Unit = {},
    onOpenReminders: () -> Unit = {},
    onPickAvatar: () -> Unit = {},
    onImportPhotos: () -> Unit = {},
    onOpenGallery: () -> Unit = {},
    onOpenEvent: (EventData) -> Unit = {},
    onOpenEventList: () -> Unit = {},
    onOpenMemoryBook: () -> Unit = {},
    onOpenCalendar: (highlightMillis: Long?) -> Unit = {},
    onOpenMoments: () -> Unit = {},
    onTestNotification: () -> Unit = {},
    onTestMesversario: () -> Unit = {},
    onTestAniversario: () -> Unit = {},
    onSignOutClick: () -> Unit,
    onBack: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize().background(CozyCream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            // ═══ TOP SAGE BAND com borda inferior curvada ═══
            SageHeader(
                profile = childProfile,
                userData = userData,
                allPhotos = allPhotos,
                onLoadPhoto = onLoadPhoto,
                onSettingsClick = onBack,
                onCreateRegistro = onRegisterEvent,
                onQuickRegister = onQuickRegister,
                onAddReminder = onAddReminder,
                onPickAvatar = onPickAvatar
            )

            // ═══ BODY ═══
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                // Próximo marco em destaque
                if (childProfile != null) {
                    NextMilestoneCard(profile = childProfile, onClick = { targetMillis ->
                        onOpenCalendar(targetMillis)
                    })
                }

                // Últimos marcos — "ver todos" navega pra EventList (filtrada como marcos)
                LatestMarcos(
                    events = events,
                    moments = moments,
                    allPhotos = allPhotos,
                    onLoadPhoto = onLoadPhoto,
                    onSeeAll = onOpenEventList,
                    onOpenEvent = onOpenEvent
                )

                // Atalhos
                ShortcutsGrid(
                    onCreate = onRegisterEvent,
                    onMoments = onOpenMoments,
                    onMemoryBook = onOpenMemoryBook,
                    onCalendar = { onOpenCalendar(null) },
                    onReminders = onOpenReminders
                )

                // Footer extras
                FooterCard(
                    onTestNotification = onTestNotification,
                    onTestMesversario = onTestMesversario,
                    onTestAniversario = onTestAniversario,
                    onSignOut = onSignOutClick
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

/** Shape com borda inferior em curva única e suave — bowl shape. */
private val WavyBottomShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    val curveDepth = 36f
    val baseY = h - curveDepth
    moveTo(0f, 0f)
    lineTo(w, 0f)
    lineTo(w, baseY)
    // Uma curva única descendo no centro — quadrática simétrica.
    quadraticBezierTo(w / 2f, h, 0f, baseY)
    lineTo(0f, 0f)
    close()
}

@Composable
private fun SageHeader(
    profile: ChildProfile?,
    userData: UserData,
    allPhotos: List<com.tsrapprun.camera.PhotoData>,
    onLoadPhoto: suspend (com.tsrapprun.camera.PhotoData) -> ByteArray?,
    onSettingsClick: () -> Unit,
    onCreateRegistro: () -> Unit = {},
    onQuickRegister: () -> Unit = {},
    onAddReminder: () -> Unit = {},
    onPickAvatar: () -> Unit = {}
) {
    val avatarPhoto = remember(profile?.avatarPhotoId, allPhotos) {
        profile?.avatarPhotoId?.let { id -> allPhotos.find { it.id == id } }
    }
    val avatarBitmap = com.tsrapprun.ui.photos.rememberPhotoBitmap(avatarPhoto, onLoadPhoto)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(WavyBottomShape)
            .background(CozySage)
            .padding(horizontal = 24.dp, vertical = 14.dp)
            .padding(bottom = 26.dp)  // compensa as ondas mais profundas
    ) {
        // Blobs decorativos no canto superior direito
        DarkBlob(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-60).dp)
                .size(180.dp),
            color = CozyOliveDeep,
            alpha = 0.20f
        )
        DarkBlob(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = 30.dp)
                .size(120.dp),
            color = CozyOliveDeep,
            alpha = 0.14f
        )

        Column {
            // Linha topo: branding "meu tesourinho" + settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = italicSerifText(
                        prefix = "meu ",
                        italic = "tesourinho",
                        italicColor = Butter,
                        defaultColor = CozyCream,
                        italicWeight = FontWeight.Bold
                    ),
                    fontFamily = FontFamily.Serif,
                    fontSize = 18.sp,
                    letterSpacing = (-0.3).sp
                )
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clickable(onClick = onSettingsClick),
                    contentAlignment = Alignment.Center
                ) {
                    SettingsIcon(color = CozyCream)
                }
            }

            Spacer(Modifier.height(20.dp))

            // Avatar + saudação
            val parentName = profile?.parentFirstName?.takeIf { it.isNotBlank() }
                ?: userData.displayName?.split(" ")?.firstOrNull()
            val timeGreeting = greetingByTime()
            val childName = profile?.firstName

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(CozyCream)
                        .clickable(onClick = onPickAvatar)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Peach),
                        contentAlignment = Alignment.Center
                    ) {
                        if (avatarBitmap != null) {
                            androidx.compose.foundation.Image(
                                bitmap = avatarBitmap,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else if (childName != null) {
                            Text(
                                childName.first().uppercase(),
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Medium,
                                color = CozyAmberDeep
                            )
                        }
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    // "Bom dia, [pai/mãe]"
                    Text(
                        text = if (parentName != null) {
                            italicSerifText(
                                prefix = "$timeGreeting, ",
                                italic = parentName,
                                italicColor = Butter,
                                defaultColor = CozyCream,
                                italicWeight = FontWeight.Bold
                            )
                        } else {
                            italicSerifText(
                                italic = timeGreeting,
                                italicColor = Butter,
                                defaultColor = CozyCream,
                                italicWeight = FontWeight.Bold
                            )
                        },
                        fontFamily = FontFamily.Serif,
                        fontSize = 22.sp,
                        lineHeight = 26.sp,
                        letterSpacing = (-0.4).sp
                    )
                    Spacer(Modifier.height(4.dp))
                    // "o que [filho] fez hoje?"
                    Text(
                        text = if (childName != null) {
                            italicSerifText(
                                prefix = "o que ",
                                italic = childName,
                                suffix = " fez hoje?",
                                italicColor = Butter,
                                defaultColor = CozyCream.copy(alpha = 0.85f),
                                italicWeight = FontWeight.Bold
                            )
                        } else {
                            italicSerifText(
                                italic = "o que aconteceu hoje?",
                                italicColor = Butter,
                                defaultColor = CozyCream.copy(alpha = 0.85f)
                            )
                        },
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        letterSpacing = (-0.1).sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // CTA principal "+ novo registro" + atalho "registro rápido" (mic)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onCreateRegistro),
                    shape = RoundedCornerShape(999.dp),
                    color = CozyCream,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "+",
                            fontFamily = FontFamily.Serif,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = OliveDeep
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = italicSerifText(
                                prefix = "novo ",
                                italic = "registro",
                                italicColor = CozyAmberDeep,
                                defaultColor = OliveDeep,
                                italicWeight = FontWeight.Bold
                            ),
                            fontFamily = FontFamily.Serif,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                // Botão "registro rápido" — mic em butter sólido
                Surface(
                    modifier = Modifier
                        .size(52.dp)
                        .clickable(onClick = onQuickRegister),
                    shape = CircleShape,
                    color = Butter,
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🎙", fontSize = 22.sp)
                    }
                }
                // Botão "lembrete rápido" — bell em peach sólido
                Surface(
                    modifier = Modifier
                        .size(52.dp)
                        .clickable(onClick = onAddReminder),
                    shape = CircleShape,
                    color = Peach,
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🔔", fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

/** Cor olive escura — adicionada localmente para o blob. */
private val CozyOliveDeep = Color(0xFF3F4F2E)

/** Blob orgânico desenhado via Canvas — círculo deformado em sin. */
@Composable
private fun DarkBlob(modifier: Modifier, color: Color, alpha: Float) {
    Box(
        modifier = modifier.background(
            color = color.copy(alpha = alpha),
            shape = CircleShape
        )
    )
}

/** Ícone settings line-style desenhado em Canvas (sem emoji). */
@Composable
private fun SettingsIcon(color: Color) {
    androidx.compose.foundation.Canvas(
        modifier = Modifier.size(20.dp)
    ) {
        val cx = size.width / 2
        val cy = size.height / 2
        val r = size.minDimension * 0.18f
        // Círculo central
        drawCircle(
            color = color,
            radius = r,
            center = androidx.compose.ui.geometry.Offset(cx, cy),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.6.dp.toPx())
        )
        // 8 raios curtos ao redor
        val outerR = size.minDimension * 0.45f
        val gapR = size.minDimension * 0.30f
        for (i in 0 until 8) {
            val angle = i * (kotlin.math.PI / 4).toFloat()
            val sx = cx + gapR * kotlin.math.cos(angle.toDouble()).toFloat()
            val sy = cy + gapR * kotlin.math.sin(angle.toDouble()).toFloat()
            val ex = cx + outerR * kotlin.math.cos(angle.toDouble()).toFloat()
            val ey = cy + outerR * kotlin.math.sin(angle.toDouble()).toFloat()
            drawLine(
                color = color,
                start = androidx.compose.ui.geometry.Offset(sx, sy),
                end = androidx.compose.ui.geometry.Offset(ex, ey),
                strokeWidth = 1.6.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
    }
}

@Composable
private fun ChildTitle(profile: ChildProfile) {
    val now = nowMillis()
    val age = remember(profile) { AgeCalculator.calculateAge(profile, now) }
    val accentText = ageAccent(age)

    Text(
        buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = CozyCream,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Normal
                )
            ) {
                append("${profile.firstName}, ")
            }
            withStyle(
                SpanStyle(
                    color = Butter,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Light
                )
            ) {
                append(accentText)
            }
        },
        fontSize = 28.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.6).sp
    )
    val sub = ageSubtitle(age)
    if (sub.isNotEmpty()) {
        Spacer(Modifier.height(2.dp))
        Text(
            "~ $sub",
            fontFamily = FontFamily.Cursive,
            fontSize = 16.sp,
            color = CozyCream.copy(alpha = 0.85f)
        )
    }
}

private fun ageAccent(age: com.tsrapprun.child.Age): String = when (age.phase) {
    LifePhase.PREGNANCY -> "${age.pregnancyWeeksRemaining}sem pra chegar"
    LifePhase.NEWBORN -> "${age.daysOfLife} ${if (age.daysOfLife == 1) "dia" else "dias"}"
    LifePhase.BABY -> "${age.months} ${if (age.months == 1) "mês" else "meses"}"
    LifePhase.TODDLER -> "${age.years} ${if (age.years == 1) "aninho" else "aninhos"}"
}

private fun ageSubtitle(age: com.tsrapprun.child.Age): String = when (age.phase) {
    LifePhase.NEWBORN -> "${age.daysOfLife} dias de vida"
    LifePhase.BABY -> "no caminho do primeiro aniversário"
    else -> ""
}

// ════════════════════════════════════════════════════════════
// PRÓXIMO MARCO — destaque butter
// ════════════════════════════════════════════════════════════

@Composable
private fun NextMilestoneCard(profile: ChildProfile, onClick: (targetMillis: Long) -> Unit) {
    val now = nowMillis()
    val next = remember(profile) { AgeCalculator.nextMilestone(profile, now) }
    val abbrev = remember(next.targetMillis) {
        val c = dateComponentsOf(next.targetMillis)
        val short = listOf("jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez")
        "${c.day} ${short[c.monthIndex]}"
    }
    val isMesversario = next.label == "mesversário"
    val title = when {
        isMesversario -> "${profile.firstName} faz mais um mês"
        next.label == "aniversário" -> "${profile.firstName} faz aniversário"
        else -> "${profile.firstName} chega ao mundo"
    }

    Box {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(next.targetMillis) },
            shape = RoundedCornerShape(18.dp),
            color = Butter,
            border = BorderStroke(1.4.dp, CozyAmberDeep.copy(alpha = 0.33f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 80.dp, top = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("✿", fontSize = 32.sp, color = CozyAmberDeep)
                Spacer(Modifier.width(14.dp))
                Column {
                    Tag("em ${next.daysUntil} dias", color = CozyAmberDeep)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        title,
                        fontFamily = FontFamily.Serif,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = CozyAmberDeep,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        abbrev,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = CozyAmberDeep.copy(alpha = 0.7f)
                    )
                }
            }
        }
        // Stamp no canto direito
        Stamp(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 4.dp, top = 4.dp),
            label = "${next.daysUntil}d",
            sub = "próx.",
            color = CozyAmberDeep,
            rotation = 10f,
            size = 56.dp
        )
    }
}

// ════════════════════════════════════════════════════════════
// ÚLTIMOS REGISTROS
// ════════════════════════════════════════════════════════════

@Composable
private fun LatestMarcos(
    events: List<EventData>,
    moments: List<com.tsrapprun.moments.MomentEntry>,
    allPhotos: List<com.tsrapprun.camera.PhotoData>,
    onLoadPhoto: suspend (com.tsrapprun.camera.PhotoData) -> ByteArray?,
    onSeeAll: () -> Unit,
    onOpenEvent: (EventData) -> Unit
) {
    val recent = remember(events, moments) {
        // Marcos = events com isMilestone OU moments auto/marcados.
        val milestoneEvents = events.filter { it.isMilestone }
        milestoneEvents.sortedByDescending { it.createdAt }.take(2)
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                italicSerifText(
                    prefix = "últimos ",
                    italic = "marcos",
                    italicColor = CozyAmberDeep,
                    defaultColor = OliveDeep
                ),
                fontSize = 22.sp,
                lineHeight = 26.sp,
                letterSpacing = (-0.4).sp
            )
            Surface(
                modifier = Modifier.clickable(onClick = onSeeAll),
                shape = RoundedCornerShape(999.dp),
                color = OliveDeep.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, OliveDeep.copy(alpha = 0.18f))
            ) {
                Text(
                    "ver todos →",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = OliveDeep,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (recent.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = CozyCreamDeep,
                border = BorderStroke(1.4.dp, OliveDeep.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Tag("ainda sem marcos", color = CozyOlive)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "marque um registro como marco quando for especial ✦",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 13.sp,
                        color = CozyOlive
                    )
                }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                recent.forEach { event ->
                    val thumb = remember(event.thumbnailPhotoId, allPhotos) {
                        allPhotos.find { it.id == event.thumbnailPhotoId }
                    }
                    EventMiniCard(
                        modifier = Modifier.weight(1f),
                        event = event,
                        thumbnailPhoto = thumb,
                        onLoadPhoto = onLoadPhoto,
                        onClick = { onOpenEvent(event) }
                    )
                }
                if (recent.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun EventMiniCard(
    modifier: Modifier,
    event: EventData,
    thumbnailPhoto: com.tsrapprun.camera.PhotoData?,
    onLoadPhoto: suspend (com.tsrapprun.camera.PhotoData) -> ByteArray?,
    onClick: () -> Unit
) {
    val bitmap = com.tsrapprun.ui.photos.rememberPhotoBitmap(thumbnailPhoto, onLoadPhoto)
    Surface(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = CozyCream,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, OliveDeep.copy(alpha = 0.06f))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(8.dp))
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
            Spacer(Modifier.height(8.dp))
            Tag("evento", color = CozyAmberDeep, fontSize = 8.5.sp)
            Spacer(Modifier.height(2.dp))
            Text(
                event.name,
                fontFamily = FontFamily.Serif,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = OliveDeep,
                lineHeight = 17.sp,
                maxLines = 1,
                letterSpacing = (-0.2).sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "${event.photoCount} fotos · ${formatShortDate(event.createdAt)}",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.5.sp,
                color = CozyOlive.copy(alpha = 0.6f),
                letterSpacing = 0.4.sp
            )
        }
    }
}

private fun formatShortDate(epochMillis: Long): String {
    val c = dateComponentsOf(epochMillis)
    val short = listOf("jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez")
    return "${c.day} ${short[c.monthIndex]}"
}

// ════════════════════════════════════════════════════════════
// ATALHOS — 2x2 grid colorido
// ════════════════════════════════════════════════════════════

@Composable
private fun ShortcutsGrid(
    onCreate: () -> Unit,
    onMoments: () -> Unit,
    onMemoryBook: () -> Unit,
    onCalendar: () -> Unit,
    onReminders: () -> Unit
) {
    Column {
        Tag("atalhos", color = OliveDeep)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ShortcutTile(
                modifier = Modifier.weight(1f),
                label = "linha do tempo",
                symbol = "☰",
                bg = Peach,
                fg = CozyAmberDeep,
                onClick = onMoments
            )
            ShortcutTile(
                modifier = Modifier.weight(1f),
                label = "calendário",
                symbol = "▤",
                bg = Butter,
                fg = CozyAmberDeep,
                onClick = onCalendar
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ShortcutTile(
                modifier = Modifier.weight(1f),
                label = "livro de memórias",
                symbol = "▦",
                bg = CozyCreamDeep,
                fg = OliveDeep,
                onClick = onMemoryBook
            )
            ShortcutTile(
                modifier = Modifier.weight(1f),
                label = "lembretes",
                symbol = "✦",
                bg = CozySageMist,
                fg = CozyOlive,
                onClick = onReminders
            )
        }
    }
}

@Composable
private fun ShortcutTile(
    modifier: Modifier,
    label: String,
    symbol: String,
    bg: Color,
    fg: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(64.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = bg
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(symbol, fontSize = 20.sp, color = fg, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(10.dp))
            Text(
                label,
                fontFamily = FontFamily.Serif,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = fg,
                letterSpacing = (-0.2).sp,
                lineHeight = 16.sp
            )
        }
    }
}

// ════════════════════════════════════════════════════════════
// FOOTER (test notification + sair)
// ════════════════════════════════════════════════════════════

@Composable
private fun DebugRow(symbol: String, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = CozyCreamDeep,
        border = BorderStroke(1.4.dp, OliveDeep.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(symbol, fontSize = 18.sp)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = OliveDeep
                )
                Text(
                    subtitle,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.5.sp,
                    color = CozyOlive.copy(alpha = 0.6f),
                    letterSpacing = 0.4.sp
                )
            }
            Text("›", fontSize = 16.sp, color = CozyOlive, fontWeight = FontWeight.Light)
        }
    }
}

@Composable
private fun FooterCard(
    onTestNotification: () -> Unit,
    onTestMesversario: () -> Unit,
    onTestAniversario: () -> Unit,
    onSignOut: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Tag("debug · testes", color = OliveDeep)
        DebugRow("🔔", "testar notificação (5s)", "deixe o app em segundo plano", onTestNotification)
        DebugRow("✦", "preview mesversário", "fala 'mais um mês completinho'", onTestMesversario)
        DebugRow("🎉", "preview aniversário", "fala 'mais um ano de vida'", onTestAniversario)
    }
    Spacer(Modifier.height(8.dp))
    Text(
        "sair da conta",
        fontFamily = FontFamily.Serif,
        fontStyle = FontStyle.Italic,
        fontSize = 13.sp,
        color = Color(0xFFB85450),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSignOut)
            .padding(vertical = 10.dp),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}

private fun greetingByTime(): String {
    val hour = com.tsrapprun.platform.dateComponentsOf(com.tsrapprun.platform.nowMillis()).hour
    return when {
        hour in 5..11 -> "bom dia"
        hour in 12..17 -> "boa tarde"
        else -> "boa noite"
    }
}
