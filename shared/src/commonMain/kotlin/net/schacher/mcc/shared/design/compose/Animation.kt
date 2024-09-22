package net.schacher.mcc.shared.design.compose

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith

object Animation {
    val fullscreenEnter = (slideInVertically { height -> height } + fadeIn())

    val fullscreenExit = slideOutVertically { height -> height } + fadeOut()

    val fullscreenTransition = (fullscreenEnter).togetherWith(fullscreenExit)
}
