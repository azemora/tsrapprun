/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  ChildRegistrationScreen.kt — tradução de                    ║
 * ║  fullapp/screens/child-registration.jsx                      ║
 * ║                                                              ║
 * ║  Header editorial "vamos conhecer seu pequeno." + foto       ║
 * ║  upload polaroide circular + segmented já nasceu/gestação +  ║
 * ║  campos de nome e data com tag monospace.                    ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.child

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.platform.dateComponentsOf
import com.tsrapprun.platform.epochMillisFromComponents
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
import com.tsrapprun.ui.theme.CozyOlive
import com.tsrapprun.ui.theme.CozySageMist

@Composable
fun ChildRegistrationScreen(
    initialProfile: ChildProfile? = null,
    onSave: (firstName: String, birthdateMillis: Long, isPregnancy: Boolean, parentFirstName: String) -> Unit,
    onCancel: (() -> Unit)? = null,
    onPickAvatar: () -> Unit = {}
) {
    var firstName by remember { mutableStateOf(initialProfile?.firstName ?: "") }
    var parentFirstName by remember { mutableStateOf(initialProfile?.parentFirstName ?: "") }
    var isPregnancy by remember { mutableStateOf(initialProfile?.isPregnancy ?: false) }

    val now = nowMillis()
    val nowComponents = dateComponentsOf(now)
    val initial = initialProfile?.birthdateMillis?.let(::dateComponentsOf)

    var day by remember { mutableStateOf(initial?.day?.toString() ?: "") }
    var month by remember { mutableStateOf(initial?.let { (it.monthIndex + 1).toString() } ?: "") }
    var year by remember { mutableStateOf(initial?.year?.toString() ?: nowComponents.year.toString()) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(CozyCream)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            ScreenHeader(
                chapter = "cadastro — passo 1 de 2",
                title = italicSerifText(
                    prefix = "vamos conhecer\n",
                    italic = "seu pequeno",
                    suffix = ".",
                    italicColor = CozyAmberDeep,
                    defaultColor = OliveDeep
                ),
                subtitle = "esses detalhes ficam só no seu aparelho. você pode editar depois.",
                onBack = onCancel
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Foto upload polaroide circular
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    PhotoUploadPlaceholder(onClick = onPickAvatar)
                }

                // Segmented já nasceu / gestação
                Column {
                    Tag("momento", color = OliveDeep)
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = CozyCreamDeep
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            SegmentedOption(
                                modifier = Modifier.weight(1f),
                                label = "já nasceu",
                                symbol = "👶",
                                selected = !isPregnancy,
                                onClick = { isPregnancy = false }
                            )
                            SegmentedOption(
                                modifier = Modifier.weight(1f),
                                label = "gestação",
                                symbol = "🤰",
                                selected = isPregnancy,
                                onClick = { isPregnancy = true }
                            )
                        }
                    }
                }

                // Seu nome (papai/mamãe)
                Column {
                    Tag("seu nome (como podemos te chamar)", color = OliveDeep)
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = CozyCreamDeep,
                        border = BorderStroke(1.4.dp, CozyOlive.copy(alpha = 0.2f))
                    ) {
                        BasicTextField(
                            value = parentFirstName,
                            onValueChange = { if (it.length <= 50) parentFirstName = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
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
                                if (parentFirstName.isEmpty()) {
                                    Text(
                                        "ex: papai · mamãe · Aracelli",
                                        fontFamily = FontFamily.Serif,
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 16.sp,
                                        color = CozyOlive.copy(alpha = 0.4f)
                                    )
                                }
                                inner()
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                    }
                }

                // Nome do filho
                Column {
                    Tag("nome ou apelido do bebê", color = OliveDeep)
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = CozyCreamDeep,
                        border = BorderStroke(1.4.dp, CozyOlive.copy(alpha = 0.2f))
                    ) {
                        BasicTextField(
                            value = firstName,
                            onValueChange = { if (it.length <= 50) firstName = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
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
                                if (firstName.isEmpty()) {
                                    Text(
                                        if (isPregnancy) "ex: o nome escolhido" else "ex: Manu",
                                        fontFamily = FontFamily.Serif,
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 16.sp,
                                        color = CozyOlive.copy(alpha = 0.4f)
                                    )
                                }
                                inner()
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                    }
                }

                // Data
                Column {
                    Tag(if (isPregnancy) "data prevista de parto" else "nasceu em", color = OliveDeep)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DateField(modifier = Modifier.weight(0.7f), value = day, label = "dia") {
                            day = it.filter { c -> c.isDigit() }.take(2)
                        }
                        DateField(modifier = Modifier.weight(0.7f), value = month, label = "mês") {
                            month = it.filter { c -> c.isDigit() }.take(2)
                        }
                        DateField(modifier = Modifier.weight(1f), value = year, label = "ano") {
                            year = it.filter { c -> c.isDigit() }.take(4)
                        }
                    }
                    val displayDate = formatPretty(day, month, year)
                    if (displayDate != null) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "~ $displayDate ✿",
                            fontFamily = FontFamily.Cursive,
                            fontSize = 16.sp,
                            color = CozyAmberDeep,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                        )
                    }
                }

                if (errorMessage != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF5D9D7)
                    ) {
                        Text(
                            errorMessage ?: "",
                            fontSize = 13.sp,
                            color = Color(0xFFB85450),
                            modifier = Modifier.padding(14.dp)
                        )
                    }
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
                    text = "continuar",
                    onClick = {
                        val parsedMillis = parseBirthdate(day, month, year)
                        if (parsedMillis == null) {
                            errorMessage = "data inválida — confira dia, mês e ano."
                            return@PrimaryButton
                        }
                        when (val r = ChildProfileSanitizer.sanitize(
                            rawName = firstName,
                            birthdateMillis = parsedMillis,
                            isPregnancy = isPregnancy,
                            nowMillis = nowMillis()
                        )) {
                            is ChildProfileSanitizer.Result.Invalid -> errorMessage = r.message
                            is ChildProfileSanitizer.Result.Valid -> {
                                errorMessage = null
                                onSave(r.firstName, r.birthdateMillis, r.isPregnancy, parentFirstName.trim())
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PhotoUploadPlaceholder(onClick: () -> Unit) {
    Box(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(130.dp)
                .graphicsLayer { rotationZ = -3f }
                .clip(CircleShape)
                .background(CozyCream)
                .border(BorderStroke(1.dp, CozyOlive.copy(alpha = 0.4f)), CircleShape)
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(CozySageMist),
                contentAlignment = Alignment.Center
            ) {
                Text("📷", fontSize = 32.sp)
            }
        }
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 4.dp, y = 4.dp)
                .graphicsLayer { rotationZ = 8f },
            shape = RoundedCornerShape(999.dp),
            color = CozyAmber,
            shadowElevation = 6.dp
        ) {
            Text(
                "+ foto",
                fontFamily = FontFamily.Serif,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = CozyCream,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun SegmentedOption(
    modifier: Modifier,
    label: String,
    symbol: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(11.dp),
        color = if (selected) OliveDeep else Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(symbol, fontSize = 14.sp)
            Spacer(Modifier.width(6.dp))
            Text(
                label,
                fontFamily = FontFamily.Serif,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (selected) CozyCream else CozyOlive.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun DateField(
    modifier: Modifier,
    value: String,
    label: String,
    onValueChange: (String) -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = CozyCreamDeep,
        border = BorderStroke(1.4.dp, CozyOlive.copy(alpha = 0.15f))
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            singleLine = true,
            cursorBrush = SolidColor(CozyAmber),
            textStyle = TextStyle(
                fontFamily = FontFamily.Serif,
                fontSize = 16.sp,
                color = OliveDeep,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            ),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(
                        label,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 14.sp,
                        color = CozyOlive.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                inner()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
            )
        )
    }
}

private fun parseBirthdate(day: String, month: String, year: String): Long? {
    val d = day.toIntOrNull() ?: return null
    val m = month.toIntOrNull() ?: return null
    val y = year.toIntOrNull() ?: return null
    if (d !in 1..31 || m !in 1..12 || y !in 1900..3000) return null
    return runCatching {
        epochMillisFromComponents(year = y, monthIndex = m - 1, day = d, hour = 0, minute = 0)
    }.getOrNull()
}

private fun formatPretty(day: String, month: String, year: String): String? {
    val d = day.toIntOrNull() ?: return null
    val m = month.toIntOrNull() ?: return null
    val y = year.toIntOrNull() ?: return null
    val months = listOf(
        "janeiro", "fevereiro", "março", "abril", "maio", "junho",
        "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
    )
    if (m !in 1..12 || d !in 1..31 || y !in 1900..3000) return null
    return "$d de ${months[m - 1]} de $y"
}
