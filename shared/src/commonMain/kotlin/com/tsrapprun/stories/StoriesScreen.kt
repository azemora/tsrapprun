/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  StoriesScreen.kt — tradução de fullapp/screens/stories.jsx  ║
 * ║                                                              ║
 * ║  Hero sage com história em destaque + biblioteca em lista.   ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.stories

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.ui.chrome.Butter
import com.tsrapprun.ui.chrome.Lilac
import com.tsrapprun.ui.chrome.OliveDeep
import com.tsrapprun.ui.chrome.Pastels
import com.tsrapprun.ui.chrome.Peach
import com.tsrapprun.ui.chrome.Polaroid
import com.tsrapprun.ui.chrome.Sky
import com.tsrapprun.ui.chrome.Tag
import com.tsrapprun.ui.chrome.TornDivider
import com.tsrapprun.ui.chrome.italicSerifText
import com.tsrapprun.ui.theme.CozyAmberDeep
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozyCreamDeep
import com.tsrapprun.ui.theme.CozyOlive
import com.tsrapprun.ui.theme.CozySage

private data class Story(
    val title: String,
    val titleAccent: String,
    val duration: String,
    val emoji: String,
    val tone: Color,
    val tag: String
)

private val STORIES = listOf(
    Story("O Patinho", "Amigo", "4 min", "🦆", Butter, "amizade"),
    Story("A Lua", "Gentil", "3 min", "🌙", Sky, "soninho"),
    Story("A Festa das", "Folhas", "5 min", "🍃", Pastels.sage, "natureza"),
    Story("O Sol que", "Sorri", "3 min", "☀", Peach, "alegria"),
    Story("Pequenas", "Estrelas", "4 min", "✦", Lilac, "soninho")
)

@Composable
fun StoriesScreen(
    onBack: () -> Unit,
    onOpenStory: (storyId: String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CozyCream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            FeaturedHero(featured = STORIES[0], onBack = onBack)

            // Lista
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            italicSerifText(
                                prefix = "biblioteca ",
                                italic = "de histórias",
                                defaultColor = OliveDeep,
                                italicColor = CozyAmberDeep
                            ),
                            fontSize = 22.sp,
                            lineHeight = 26.sp,
                            letterSpacing = (-0.4).sp
                        )
                    }
                }
                items(STORIES.drop(1), key = { it.title }) { story ->
                    Spacer(Modifier.height(10.dp))
                    StoryRow(story, onClick = { onOpenStory(story.title) })
                }
            }
        }
    }
}

@Composable
private fun FeaturedHero(featured: Story, onBack: () -> Unit) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CozySage)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.size(36.dp).clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Text("‹", fontSize = 24.sp, fontWeight = FontWeight.Light, color = CozyCream)
                }
                Tag("histórias para o soninho", color = CozyCream)
                Spacer(Modifier.size(36.dp))
            }

            Spacer(Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Polaroid(
                    tone = featured.tone,
                    width = 110.dp,
                    photoHeight = 110.dp,
                    rotation = -4f
                )
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Tag("história · ${featured.duration}", color = CozyCream)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(color = CozyCream, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Normal)) {
                                append(featured.title + "\n")
                            }
                            withStyle(SpanStyle(color = Butter, fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Light)) {
                                append(featured.titleAccent)
                            }
                        },
                        fontSize = 26.sp,
                        lineHeight = 27.sp,
                        letterSpacing = (-0.7).sp
                    )
                    Spacer(Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = CozyCream
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "ler agora",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = OliveDeep
                            )
                            Spacer(Modifier.width(6.dp))
                            Text("→", fontSize = 14.sp, color = OliveDeep, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
        }
        TornDivider(color = CozyCream, height = 14.dp)
    }
}

@Composable
private fun StoryRow(story: Story, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = CozyCreamDeep,
        border = BorderStroke(1.4.dp, OliveDeep.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini polaroid emoji
            Box(
                modifier = Modifier
                    .size(width = 56.dp, height = 64.dp)
                    .graphicsLayer { rotationZ = -2f }
                    .clip(RoundedCornerShape(6.dp))
                    .background(story.tone)
                    .border(BorderStroke(1.4.dp, CozyCream), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(story.emoji, fontSize = 28.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Tag(story.tag, color = OliveDeep)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "· ${story.duration}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.5.sp,
                        color = CozyOlive.copy(alpha = 0.55f)
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    "${story.title} ${story.titleAccent}",
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = OliveDeep,
                    letterSpacing = (-0.3).sp
                )
            }
            // Botão "ler"
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(CozyCream)
                    .border(BorderStroke(1.4.dp, OliveDeep.copy(alpha = 0.2f)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("→", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OliveDeep)
            }
        }
    }
}

// border é importado abaixo
