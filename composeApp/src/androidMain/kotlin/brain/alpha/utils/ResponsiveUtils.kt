package brain.alpha.utils

import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.runtime.Composable

@Composable
actual fun isCompactScreen(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp < 600
}