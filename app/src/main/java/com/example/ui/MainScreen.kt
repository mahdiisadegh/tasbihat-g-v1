package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import com.example.model.DhikrScreenState
import com.example.ui.components.FinalScreenOverlay
import com.example.ui.components.GlassBottomRail
import com.example.ui.components.SettingsDialog

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val screenState by viewModel.screenState.collectAsState()
    val isMusicEnabled by viewModel.isMusicEnabled.collectAsState()
    val isDhikrSoundEnabled by viewModel.isDhikrSoundEnabled.collectAsState()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
    val stepSeq by viewModel.stepSequenceNumber.collectAsState()

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                viewModel.onScreenTap()
            }
            .testTag("main_screen_container")
    ) {
        // 1. Fullscreen Image Canvas with Horizontal Slide Transition
        AnimatedContent(
            targetState = screenState to stepSeq,
            transitionSpec = {
                val enterAnim = slideInHorizontally(
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                    initialOffsetX = { fullWidth -> fullWidth }
                ) + fadeIn(animationSpec = tween(durationMillis = 350))

                val exitAnim = slideOutHorizontally(
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                    targetOffsetX = { fullWidth -> -fullWidth }
                ) + fadeOut(animationSpec = tween(durationMillis = 350))

                enterAnim.togetherWith(exitAnim)
            },
            label = "ScreenImageTransition",
            modifier = Modifier.fillMaxSize()
        ) { (state, _) ->
            val imageResId = when (state) {
                is DhikrScreenState.Splash -> viewModel.imageRepository.getSplashImageResId()
                is DhikrScreenState.Active -> viewModel.imageRepository.getImageResIdForStep(
                    state.section,
                    state.currentIndex
                )
                is DhikrScreenState.Completed -> viewModel.imageRepository.getFinalImageResId()
            }

            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // 2. Subtle gradient overlay to enhance readability at top and bottom edges
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x33FFF8F4),
                            Color.Transparent,
                            Color(0x44231A11)
                        )
                    )
                )
        )

        // 3. UI Controls depending on Screen State
        when (val state = screenState) {
            is DhikrScreenState.Splash -> {
                // Initial splash: purely peaceful image for 2 seconds, no buttons
            }
            is DhikrScreenState.Active -> {
                GlassBottomRail(
                    section = state.section,
                    currentIndex = state.currentIndex,
                    onSettingsClick = { viewModel.setSettingsOpen(true) },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
            is DhikrScreenState.Completed -> {
                FinalScreenOverlay(
                    onRestartClick = { viewModel.restart() },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }

        // 4. Settings Dialog
        if (isSettingsOpen) {
            SettingsDialog(
                isMusicEnabled = isMusicEnabled,
                onMusicToggle = { viewModel.setMusicEnabled(it) },
                isDhikrSoundEnabled = isDhikrSoundEnabled,
                onDhikrSoundToggle = { viewModel.setDhikrSoundEnabled(it) },
                onRestartClick = { viewModel.restart() },
                onDismiss = { viewModel.setSettingsOpen(false) }
            )
        }
    }
}
