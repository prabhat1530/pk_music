package it.vfsfitvnm.vimusic.ui.components.themed

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import it.vfsfitvnm.vimusic.LocalPlayerAwareWindowInsets
import it.vfsfitvnm.vimusic.ui.styling.Dimensions
import it.vfsfitvnm.vimusic.ui.styling.LocalAppearance
import it.vfsfitvnm.vimusic.utils.center
import it.vfsfitvnm.vimusic.utils.color
import it.vfsfitvnm.vimusic.utils.isLandscape
import it.vfsfitvnm.vimusic.utils.semiBold
import it.vfsfitvnm.vimusic.ui.styling.PKMusicGradientColors

import androidx.compose.foundation.border

@Composable
inline fun NavigationRail(
    topIconButtonId: Int,
    noinline onTopIconButtonClick: () -> Unit,
    tabIndex: Int,
    crossinline onTabIndexChanged: (Int) -> Unit,
    content: @Composable (@Composable (Int, String, Int) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val (colorPalette, typography) = LocalAppearance.current

    val isLandscape = isLandscape

    val paddingValues = LocalPlayerAwareWindowInsets.current
        .only(WindowInsetsSides.Vertical + WindowInsetsSides.Start).asPaddingValues()

    if (isLandscape) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .background(colorPalette.background0)
                .border(0.5.dp, colorPalette.background1)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .size(
                        width = Dimensions.navigationRailWidthLandscape,
                        height = Dimensions.headerHeight
                    )
            ) {
                Image(
                    painter = painterResource(topIconButtonId),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(colorPalette.textSecondary),
                    modifier = Modifier
                        .offset(y = 48.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onTopIconButtonClick)
                        .padding(all = 12.dp)
                        .size(22.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(Dimensions.navigationRailWidthLandscape)
            ) {
                val transition = updateTransition(targetState = tabIndex, label = null)

                content { index, text, icon ->
                    val textColor by transition.animateColor(label = "") {
                        if (it == index) colorPalette.accent else colorPalette.textDisabled
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .clickable(onClick = { onTabIndexChanged(index) })
                            .padding(vertical = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(icon),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(textColor),
                            modifier = Modifier.size(24.dp)
                        )
                        BasicText(
                            text = text,
                            style = typography.xxs.semiBold.center.color(textColor)
                        )
                    }
                }
            }
        }
    } else {
        // PROPER HORIZONTAL BOTTOM BAR
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = modifier
                .background(colorPalette.background0)
                .padding(bottom = paddingValues.calculateBottomPadding())
                .padding(vertical = 12.dp)
                .fillMaxWidth()
        ) {
            val transition = updateTransition(targetState = tabIndex, label = null)

            content { index, text, icon ->
                val textColor by transition.animateColor(label = "") {
                    if (it == index) colorPalette.accent else colorPalette.textDisabled
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = { onTabIndexChanged(index) })
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(textColor),
                        modifier = Modifier.size(24.dp)
                    )
                    BasicText(
                        text = text,
                        style = typography.xxs.semiBold.center.color(textColor),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

fun Modifier.vertical(enabled: Boolean = true) =
    if (enabled)
        layout { measurable, constraints ->
            val placeable = measurable.measure(constraints.copy(maxWidth = Int.MAX_VALUE))
            layout(placeable.height, placeable.width) {
                placeable.place(
                    x = -(placeable.width / 2 - placeable.height / 2),
                    y = -(placeable.height / 2 - placeable.width / 2)
                )
            }
        } else this
