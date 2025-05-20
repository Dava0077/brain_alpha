package brain.alpha.view.concentrationView

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.LoginController
import brain.alpha.strings.Strings
import brainalpha.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import brain.alpha.controller.concentrationController.concentrationHomeController

// Concentration game home screen. Displays category options and navigation UI.
@Composable
fun concentrationHomeScreen(
    controller: concentrationHomeController,
    ) {
    // Fullscreen container with background image
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image for the screen
        Image(
            painter = painterResource(Res.drawable.fondoConcentracion),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Main layout container
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Top bar with user icon, logo, and settings button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFFEFEFEF), shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFFB0BEC5)))
            ) {

                // Left icon button (user/profile)
                IconButton(
                    onClick = { LoginController.buttonUsername() },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .size(48.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.user),
                        contentDescription = "Usuario"
                    )
                }
                // Center logo
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                        .scale(1.2f)
                )
                // Right icon button (settings)
                IconButton(
                    onClick = { LoginController.buttonSettings() },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(48.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ajustes),
                        contentDescription = "Ajustes"
                    )
                }

            }
            // Spacer after top bar
            Spacer(modifier = Modifier.height(5.dp))
            Spacer (modifier = Modifier.height(30.dp))

            // Button to go back to the home screen
            Button(
                onClick = {controller.goHome()},
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(8.dp)
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color.Black)
            ){
                Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            }

            // Spacer before section title
            Spacer(modifier = Modifier.height(30.dp))

            // Section title for concentration
            Text(
                text = Strings.t("concentration"),
                fontSize = 40.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            // Spacer before game categories
            Spacer(modifier = Modifier.height(30.dp))

            // Display the game categories (Stroop and Odd Emoji)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                brain.alpha.view.CategoryItem(
                    Strings.t("title_stroop"),
                    Res.drawable.icono_stroop,
                    Color(0xFF0647D7),
                    {controller.goStroop()}
                )
                brain.alpha.view.CategoryItem(
                    Strings.t("title_oddEmoji"),
                    Res.drawable.icono_emoji,
                    Color(0xFF0647D7),
                    {controller.goEmoji()}
                )
            }
        }
    }
}

//@Composable
//fun CategoryItem(
//    title: String,
//    image: DrawableResource,
//    borderColor: Color,
//    onClick: () -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .width(250.dp)
//            .height(270.dp)
//            .clickable { onClick() }
//            .background(borderColor, shape = RoundedCornerShape(12.dp))
//            .padding(4.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .clip(RoundedCornerShape(8.dp))
//                .background(borderColor)
//        ) {
//            Column(
//                modifier = Modifier.fillMaxSize(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.SpaceBetween
//            ) {
//                // Image block
//                Image(
//                    painter = painterResource(image),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .weight(1f)
//                        .padding(10.dp),
//                    contentScale = ContentScale.Crop
//                )
//
//                // Category title fully inside blue box
//                Text(
//                    text = title,
//                    fontSize = 14.sp,
//                    color = Color.White,
//                    fontFamily = FontFamily.SansSerif,
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(borderColor)
//                        .padding(vertical = 8.dp)
//                )
//            }
//        }
//    }
//}



