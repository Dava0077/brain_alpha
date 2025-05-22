package brain.alpha.view.memoryView

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
import brain.alpha.model.UserSession
import brain.alpha.strings.Strings
import brainalpha.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

// Main screen for the memory category. Displays user info, navigation, and game options.
@Composable
fun memoryHomeScreen(
    controller: memoryHomeController,

) {
    // Retrieve user's display name (guest or registered)
    val displayName = UserSession.getDisplayName()

    // Fullscreen container with background image
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(Res.drawable.fondoMemory),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Main content column
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Top bar with user icon, logo, settings, and display name
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
                //Username text
                Text(
                    text = "$displayName",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 72.dp)
                )
            }
            // Spacer below top bar
            Spacer(modifier = Modifier.height(5.dp)) // Espacio tras el logo

            // Back button to go to home
            Button(
                onClick = {controller.goHome()},
                modifier = Modifier
                    .align(Alignment.End)
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color(0xFFB0BEC5))
            ){
                Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
            }

            // Spacer before title
            Spacer(modifier = Modifier.height(30.dp))

            // Memory title
            Text(
                text = Strings.t("memory"),
                fontSize = 40.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )
            // Spacer before game buttons
            Spacer(modifier = Modifier.height(30.dp))

            // Category list: Couples and Simon
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                brain.alpha.view.CategoryItem(
                    Strings.t("title_couples"),
                    Res.drawable.parejas,
                    Color(0xFF0647D7),
                    {controller.goCouples()}
                )
                brain.alpha.view.CategoryItem(
                    Strings.t("title_simon"),
                    Res.drawable.img,
                    Color(0xFF0647D7),
                    {controller.goSimon()}
                )
            }
        }
    }
}
//
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



