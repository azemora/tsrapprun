/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  StoriesScreen.kt — Menu de historinhas infantis             ║
 * ║                                                              ║
 * ║  Grid 2 colunas com cards ilustrados (placeholders pastel +  ║
 * ║  emoji até termos ilustrações reais).                        ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozyInk
import com.tsrapprun.ui.theme.CozyOlive
import com.tsrapprun.ui.theme.CozyTan

// ── Catálogo placeholder de historinhas ──
private data class Story(
    val id: String,
    val title: String,
    val pages: Int,
    val emoji: String,
    val gradient: List<Color>
)

private val STORIES = listOf(
    Story("01", "O Patinho Amigo", 14, "🦆", listOf(Color(0xFFFCE4A7), Color(0xFFE8C46B))),
    Story("02", "A Lua Gentil", 12, "🌙", listOf(Color(0xFFC9DBE8), Color(0xFFA9C6DC))),
    Story("03", "A Floresta Encantada", 18, "🌳", listOf(Color(0xFFD4E0C9), Color(0xFFA8C09A))),
    Story("04", "O Sapo Curioso", 10, "🐸", listOf(Color(0xFFE0EFCB), Color(0xFFB6D58D))),
    Story("05", "A Estrela Perdida", 16, "⭐", listOf(Color(0xFFE6CFE8), Color(0xFFC9A2D2))),
    Story("06", "A Coelhinha Brincalhona", 13, "🐰", listOf(Color(0xFFF2D6D2), Color(0xFFE0AEAB))),
    Story("07", "O Velho Carvalho", 20, "🌰", listOf(Color(0xFFE8DCC4), Color(0xFFC8B998))),
    Story("08", "A Borboleta Azul", 11, "🦋", listOf(Color(0xFFD0E4F0), Color(0xFFA0C4DC)))
)

private val PageBackground = Brush.verticalGradient(
    colors = listOf(Color(0xFFEFE8F5), Color(0xFFE2DAE9))
)

@Composable
fun StoriesScreen(
    onBack: () -> Unit,
    onOpenStory: (storyId: String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp).clickable(onClick = onBack),
                    shape = CircleShape,
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CozyTan.copy(alpha = 0.5f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("←", fontSize = 18.sp, color = CozyOlive)
                    }
                }
                Spacer(Modifier.weight(1f))
                Text(
                    "Histórias",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = CozyInk,
                    letterSpacing = (-0.3).sp
                )
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.size(40.dp))
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "leituras pra acalmar e adormecer",
                fontSize = 13.sp,
                color = CozyOlive.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(20.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
            ) {
                items(STORIES, key = { it.id }) { story ->
                    StoryCard(story = story, onClick = { onOpenStory(story.id) })
                }
            }
        }
    }
}

@Composable
private fun StoryCard(story: Story, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // Ilustração placeholder com gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Brush.verticalGradient(story.gradient)),
                contentAlignment = Alignment.Center
            ) {
                Text(story.emoji, fontSize = 64.sp)
            }

            // Título + páginas
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                Text(
                    story.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CozyInk,
                    lineHeight = 18.sp,
                    maxLines = 2,
                    letterSpacing = (-0.2).sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${story.pages} páginas",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = CozyOlive.copy(alpha = 0.6f),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}
