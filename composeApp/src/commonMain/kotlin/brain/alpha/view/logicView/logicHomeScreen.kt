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

@Composable
fun logicHomeScreen(
    controller: logicHomeController,
    onNavigateToHome: () -> Unit,
    onNavigateToSudoku: () -> Unit,
    onNavigateToWordS: () -> Unit
    ) {
    val displayName = UserSession.getDisplayName() // Show guest or username

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.fondoLogic),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFF222937), shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8))))
             {
                IconButton(
                    onClick = { /* Dummy action */ },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .size(55.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.user), contentDescription = "User")
                }
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center)
                        .scale(1.5f)
                )
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
            Spacer(modifier = Modifier.height(5.dp)) // Espacio tras el logo

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

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = Strings.t("logic"),
                fontSize = 40.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Category grid
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                    CategoryItem(
                        "SUDOKU",
                        Res.drawable.logica,
                        Color.White,
                        onNavigateToSudoku
                    )
                    CategoryItem(
                        Strings.t("title_wordSearch"),
                        Res.drawable.sopaLetras,
                        Color.White,
                        onNavigateToWordS
                    )
                }
            }
        }
    }

@Composable
fun CategoryItem(
    title: String,
    image: DrawableResource,
    borderColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(250.dp)
            .height(270.dp)
            .clickable { onClick() }
            .background(borderColor, shape = RoundedCornerShape(8.dp))
            .padding(4.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Image block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Category title
        Text(
            text = title,
            fontSize = 14.sp,
            color = Color.Black,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
    }
}