package com.example.meal_mate.screens

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meal_mate.R
import com.example.meal_mate.ui.theme.AuroraEnd
import com.example.meal_mate.ui.theme.AuroraMid
import com.example.meal_mate.ui.theme.AuroraStart
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinish: () -> Unit) {

    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1400),
        label = "splash-alpha"
    )

    val shimmerTransition = rememberInfiniteTransition(label = "splash-glow")
    val glow by shimmerTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = androidx.compose.animation.core.FastOutSlowInEasing)
        ),
        label = "glow"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(1700)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(AuroraStart, AuroraMid, AuroraEnd),
                    start = Offset.Zero,
                    end = Offset(1200f, 1600f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(160.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.65f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size((120 * glow).dp)
                    .alpha(alphaAnim)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "MealMate",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                modifier = Modifier.alpha(alphaAnim)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Eat smart. Spend smart.",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.alpha(alphaAnim)
            )
        }
    }
}
