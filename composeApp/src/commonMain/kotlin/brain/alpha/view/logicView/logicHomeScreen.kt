package brain.alpha.view.logicView

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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.LoginController
import brain.alpha.controller.logicController.logicHomeController
import brain.alpha.model.UserSession
import brain.alpha.strings.Strings
import brain.alpha.view.CategoryItem
import brainalpha.composeapp.generated.resources.*
import brainalpha.composeapp.generated.resources.Res
import brainalpha.composeapp.generated.resources.ajustes
import brainalpha.composeapp.generated.resources.fondoLogic
import brainalpha.composeapp.generated.resources.logo
import brainalpha.composeapp.generated.resources.user
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

// Main screen for the logic games category.
// Displays top bar, back button, section title, and logic games (e.g., Sudoku, Word Search).
@Composable
fun logicHomeScreen(
    controller: logicHomeController,
    onNavigateToHome: () -> Unit,
    onNavigateToSudoku: () -> Unit,
    onNavigateToWordS: () -> Unit
    ) {
    // Get the display name of the user (or guest)
    val displayName = UserSession.getDisplayName()

    // Fullscreen container with background image
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image for the screen
        Image(
            painter = painterResource(Res.drawable.fondoLogic),
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
                    onClick = { brain.alpha.controller.LoginController.buttonSettings() },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(55.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.ajustes), contentDescription = "Settings")
                }
                 Text(
                     text = "$displayName",
                     color = Color.White,
                     fontSize = 16.sp,
                     modifier = Modifier
                         .align(Alignment.CenterStart)
                         .padding(start = 72.dp)
                 )
            }
            //Spacer after top bar
            Spacer(modifier = Modifier.height(5.dp))

            //Back button to go home
            Button(
                onClick = { controller.goHome() },
                modifier = Modifier
                    .align(Alignment.End)
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color(0xFFB0BEC5))
            ) {
                Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            }

            // Spacer before section title
            Spacer(modifier = Modifier.height(30.dp))

            // Title: Logic
            Text(
                text = Strings.t("logic"),
                fontSize = 40.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            // Spacer before game list
            Spacer(modifier = Modifier.height(30.dp))

            // Logic game categories (Sudoku, Word Search)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                    CategoryItem(
                        "SUDOKU",
                        Res.drawable.sudoku2,
                        Color.Gray,
                        onNavigateToSudoku
                    )
                    CategoryItem(
                        Strings.t("title_wordSearch"),
                        Res.drawable.sopa,
                        Color.Gray,
                        onNavigateToWordS
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
//            .background(borderColor, shape = RoundedCornerShape(8.dp))
//            .padding(4.dp)
//            .background(Color.White, shape = RoundedCornerShape(8.dp)),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // Image block
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f)
//
//        ) {
//            Image(
//                painter = painterResource(image),
//                contentDescription = null,
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Crop
//            )
//        }
//
//        Spacer(modifier = Modifier.height(4.dp))
//
//        // Category title
//        Text(
//            text = title,
//            fontSize = 14.sp,
//            color = Color.Black,
//            fontFamily = FontFamily.SansSerif,
//            textAlign = TextAlign.Center,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 4.dp)
//        )
//    }
//}