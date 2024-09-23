package net.schacher.mcc.shared.design.compose

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

object Animation {
    val fullscreenEnter = (slideInHorizontally { width -> width } + fadeIn())

    val fullscreenExit = slideOutHorizontally { width -> width } + fadeOut()

    val fullscreenTransition = (fullscreenEnter).togetherWith(fullscreenExit)
}
