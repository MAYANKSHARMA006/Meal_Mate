package com.example.meal_mate.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meal_mate.ui.theme.AuroraEnd
import com.example.meal_mate.ui.theme.AuroraMid
import com.example.meal_mate.ui.theme.AuroraStart
import com.example.meal_mate.ui.theme.EmeraldEnd
import com.example.meal_mate.ui.theme.EmeraldStart

@Composable
fun AuroraBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "aurora-shift")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = FastOutSlowInEasing)
        ),
        label = "aurora-offset"
    )

    val gradient = Brush.linearGradient(
        colors = listOf(
            AuroraStart,
            AuroraMid,
            AuroraEnd,
            EmeraldStart.copy(alpha = 0.8f),
            EmeraldEnd.copy(alpha = 0.6f)
        ),
        start = Offset(0f, offset / 2f),
        end = Offset(offset, 1200f - offset / 3f)
    )

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.background)
        )

    Box(
        modifier = Modifier
            .matchParentSize()
            .blur(180.dp)
            .background(gradient)
    )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.72f))
        )

        content()
    }
}

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(32.dp),
    borderColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
    containerColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
    tonalElevation: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.clip(shape),
        shape = shape,
        color = containerColor,
        tonalElevation = tonalElevation,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.12f),
                            Color.White.copy(alpha = 0.02f)
                        )
                    )
                )
        ) {
            content()
        }
    }
}

@Composable
fun StatBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    valueColor: Color = MaterialTheme.colorScheme.onBackground
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 10.dp)
                )
            }
            androidx.compose.foundation.layout.Column {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = valueColor
                    )
                )
            }
        }
    }
}

