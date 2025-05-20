// commonMain/kotlin/brain/alpha/utils/ResponsiveUtils.kt
package brain.alpha.utils

import androidx.compose.runtime.Composable

/**
 * Platform-specific function to detect whether the screen is "compact".
 *
 * This is typically used to adjust UI layout for small devices like phones vs. tablets or desktops.
 *
 * The actual implementation must be provided in platform-specific source sets (e.g., androidMain, iosMain).
 *
 * @return true if the screen should be considered compact, false otherwise.
 */
@Composable
expect fun isCompactScreen(): Boolean