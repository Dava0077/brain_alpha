package brain.alpha.view.mentalAgilityView

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
import brain.alpha.controller.memoryController.memoryHomeController
import brain.alpha.controller.mentalAgilityController.mentalHomeController
import brainalpha.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

// Home screen for the Mental Agility section.
// Displays a top bar, back button, title, and two games: Mental Math and Click Number.
@Composable
fun mentalHomeScreen(
    controller: mentalHomeController,
    onNavigateToHome: () -> Unit,
    onNavigateToMath: () -> Unit,
    onNavigateToClick: () -> Unit
) {
    // Fullscreen container with background image
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image for the screen
        Image(
            painter = painterResource(Res.drawable.fondoMental),
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
                    .background(Color(0xFF222937), shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8))))
            {
                // Left icon button (user/profile)
                IconButton(
                    onClick = { LoginController.buttonUsername() },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .size(55.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.user), contentDescription = "User")
                }
                // Center logo
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center)
                        .scale(1.5f)
                )
                // Right icon button (settings)
                IconButton(
                    onClick = { LoginController.buttonSettings() },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(55.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.ajustes), contentDescription = "Settings")
                }
            }
            // Spacer after top bar
            Spacer(modifier = Modifier.height(5.dp))

            // Button to return to the main menu
            Button(
                onClick = {controller.goHome()},
                modifier = Modifier
                    .align(Alignment.End)
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF4adef8)),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color.Black)
            ){
                Text("⬅ Volver", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            }

            // Spacer before section title
            Spacer(modifier = Modifier.height(30.dp))

            // Section title
            Text(
                text = "MEMORIA",
                fontSize = 40.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            // Spacer before game buttons
            Spacer(modifier = Modifier.height(30.dp))

            // Category grid with mental games
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                brain.alpha.view.CategoryItem(
                    "MENTAL MATH",
                    Res.drawable.mentalMath,
                    Color.Black,
                    onNavigateToMath
                )
                brain.alpha.view.CategoryItem(
                    "CLICK NUMBER",
                    Res.drawable.clickNumber,
                    Color.Black,
                    onNavigateToClick
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