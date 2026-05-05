/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  ChildRegistrationScreen.kt — Cadastro do perfil             ║
 * ║                                                              ║
 * ║  Toggle: já nasceu / data prevista de parto.                 ║
 * ║  Sanitização via ChildProfileSanitizer.                      ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.child

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.platform.dateComponentsOf
import com.tsrapprun.platform.epochMillisFromComponents
import com.tsrapprun.platform.nowMillis
import com.tsrapprun.ui.theme.CozyAmber
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozyOlive
import com.tsrapprun.ui.theme.CozySage
import com.tsrapprun.ui.theme.CozySageMist
import com.tsrapprun.ui.theme.CozyTan

@Composable
fun ChildRegistrationScreen(
    initialProfile: ChildProfile? = null,
    onSave: (firstName: String, birthdateMillis: Long, isPregnancy: Boolean) -> Unit,
    onCancel: (() -> Unit)? = null
) {
    var firstName by remember { mutableStateOf(initialProfile?.firstName ?: "") }
    var isPregnancy by remember { mutableStateOf(initialProfile?.isPregnancy ?: false) }

    val now = nowMillis()
    val nowComponents = dateComponentsOf(now)
    val initial = initialProfile?.birthdateMillis?.let(::dateComponentsOf)

    var day by remember { mutableStateOf(initial?.day?.toString() ?: "") }
    var month by remember { mutableStateOf(initial?.let { (it.monthIndex + 1).toString() } ?: "") }
    var year by remember { mutableStateOf(initial?.year?.toString() ?: nowComponents.year.toString()) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CozySageMist, CozyCream)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            if (onCancel != null) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(40.dp).clickable(onClick = onCancel),
                        shape = CircleShape,
                        color = CozyCream,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CozyTan)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("←", fontSize = 18.sp, color = CozyOlive)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape).background(CozySage),
                contentAlignment = Alignment.Center
            ) {
                Text(if (isPregnancy) "🤰" else "👶", fontSize = 38.sp)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                if (initialProfile == null) "vamos começar" else "editar perfil",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = CozyOlive
            )
            Spacer(Modifier.height(6.dp))
            Text(
                if (isPregnancy) "conta um pouco sobre essa expectativa"
                else "conta um pouco sobre o seu pequeno",
                fontSize = 14.sp,
                color = CozyOlive.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(24.dp))

            // ── Toggle: já nasceu / DPP ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(CozyCream)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ToggleOption(
                    modifier = Modifier.weight(1f),
                    label = "já nasceu",
                    selected = !isPregnancy,
                    onClick = { isPregnancy = false }
                )
                ToggleOption(
                    modifier = Modifier.weight(1f),
                    label = "está pra chegar",
                    selected = isPregnancy,
                    onClick = { isPregnancy = true }
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "primeiro nome",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = CozyOlive,
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )
            OutlinedTextField(
                value = firstName,
                onValueChange = { input ->
                    if (input.length <= 50) firstName = input
                },
                placeholder = { Text(if (isPregnancy) "ex: o nome escolhido" else "ex: Maria, João, Lis") },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CozySage,
                    unfocusedBorderColor = CozyTan,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(Modifier.height(20.dp))

            Text(
                if (isPregnancy) "data prevista de parto (DPP)" else "data de nascimento",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = CozyOlive,
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DateField(
                    modifier = Modifier.weight(0.7f),
                    label = "dia",
                    value = day,
                    onValueChange = { day = it.filter { c -> c.isDigit() }.take(2) }
                )
                DateField(
                    modifier = Modifier.weight(0.7f),
                    label = "mês",
                    value = month,
                    onValueChange = { month = it.filter { c -> c.isDigit() }.take(2) }
                )
                DateField(
                    modifier = Modifier.weight(1f),
                    label = "ano",
                    value = year,
                    onValueChange = { year = it.filter { c -> c.isDigit() }.take(4) }
                )
            }

            if (errorMessage != null) {
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5D9D7)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        errorMessage ?: "",
                        fontSize = 13.sp,
                        color = Color(0xFFB85450),
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CozyCream),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CozyTan)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "🌿 ficam só no seu aparelho",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CozyOlive
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "esses dados nunca saem do dispositivo. usamos para calcular semanas, dias, mesversários e enviar lembretes locais.",
                        fontSize = 12.sp,
                        color = CozyOlive.copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    val parsedMillis = parseBirthdate(day, month, year)
                    if (parsedMillis == null) {
                        errorMessage = "data inválida — confira dia, mês e ano."
                        return@Button
                    }
                    when (val result = ChildProfileSanitizer.sanitize(
                        rawName = firstName,
                        birthdateMillis = parsedMillis,
                        isPregnancy = isPregnancy,
                        nowMillis = nowMillis()
                    )) {
                        is ChildProfileSanitizer.Result.Invalid -> {
                            errorMessage = result.message
                        }
                        is ChildProfileSanitizer.Result.Valid -> {
                            errorMessage = null
                            onSave(result.firstName, result.birthdateMillis, result.isPregnancy)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CozyAmber,
                    contentColor = Color.White
                )
            ) {
                Text(
                    if (initialProfile == null) "salvar e começar" else "salvar alterações",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ToggleOption(
    modifier: Modifier,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = if (selected) CozySage else Color.Transparent
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                label,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (selected) Color.White else CozyOlive.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun DateField(
    modifier: Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                label,
                fontSize = 13.sp,
                color = CozyOlive.copy(alpha = 0.4f),
                textAlign = TextAlign.Center
            )
        },
        singleLine = true,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CozySage,
            unfocusedBorderColor = CozyTan,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
        )
    )
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
