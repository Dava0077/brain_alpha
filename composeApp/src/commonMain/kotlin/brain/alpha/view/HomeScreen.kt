package brain.alpha.view


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import brainalpha.composeapp.generated.resources.*
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


@Serializable
object HomeRoute


@Composable
fun HomeScreen(controller: LoginController) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // Fondo general
        Image(
            painter = painterResource(Res.drawable.fondoMain),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFF222937))
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8)))


            ) {
                IconButton(
                    onClick = { controller.buttonUsername() },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .size(55.dp)
                        .background(Color(0xFF222937))
                ) {
                    Image(
                        painter = painterResource(Res.drawable.user),
                        contentDescription = "User"
                    )
                }


                IconButton(
                    onClick = { controller.buttonSettings() },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(55.dp)
                        .background(Color(0xFF222937))
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ajustes),
                        contentDescription = "Settings"
                    )
                }
            }


            Spacer(modifier = Modifier.height(70.dp)) // Espacio tras el logo


            Text(
                text = "CATEGORIES",
                fontSize = 40.sp,
                color = Color.Black,
                fontFamily = FontFamily.Cursive,
                textAlign = TextAlign.Center
            )


            Spacer(modifier = Modifier.height(24.dp))


            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CategoryItem("MEMORY", Res.drawable.memoria, Color(0xFF0647D7))
                    CategoryItem("LOGIC", Res.drawable.logica, Color(0xFF0647D7))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CategoryItem("CONCENTRATION", Res.drawable.concentracion, Color(0xFF0647D7))
                    CategoryItem("MENTAL AGILITY", Res.drawable.mentalA, Color(0xFF0647D7))
                }
            }
        }


        // Logo (fuera del box oscuro)
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopCenter)
                .absoluteOffset(y = 30.dp) // ajusta para que flote como en el diseño
                .scale(1.5f)
        )


        // Barra inferior
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 18.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(134.dp)
                    .height(5.dp)
                    .background(Color.Black, shape = RoundedCornerShape(100.dp))
            )
        }
    }
}


@Composable
fun CategoryItem(title: String, image: DrawableResource, borderColor: Color) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .height(180.dp)
            .background(borderColor, shape = RoundedCornerShape(8.dp))
            .padding(4.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }


        Spacer(modifier = Modifier.height(4.dp))


        Text(
            text = title,
            fontSize = 14.sp,
            color = Color.Black,
            fontFamily = FontFamily.Cursive,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
    }
}
