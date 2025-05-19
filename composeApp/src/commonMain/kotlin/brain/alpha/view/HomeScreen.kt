package brain.alpha.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.window.DialogProperties
import brain.alpha.controller.AuthController
import brain.alpha.controller.LoginController
import brain.alpha.model.UserSession
import brain.alpha.services.SupabaseClientProvider
import brain.alpha.strings.Strings
import brainalpha.composeapp.generated.resources.*
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/**
 * HomeScreen.kt
 *
 * This file defines the main home screen UI after a successful login.
 * It greets the user, provides navigation to different cognitive categories (Memory, Logic, etc.),
 * and includes a logout button with a confirmation dialog.
 * Uses a Scaffold with a Snackbar to show logout feedback.
 */




@Serializable
object HomeRoute

@Composable
fun HomeScreen(
    controller: LoginController,
    onNavigateToMemory: () -> Unit,
    onNavigateToLogic: () -> Unit,
    onNavigateToConcentration: () -> Unit,
    onNavigateToAgility: () -> Unit
) {
    LaunchedEffect(Unit) {
        val user = SupabaseClientProvider.client.auth.currentUserOrNull()
        if (user != null) {
            UserSession.setUserId(user.id)
            println("✅ Sesión restaurada correctamente: ${user.email}")
        } else {
            println("⚠ Usuario no logueado")
        }
    }
    val displayName = UserSession.getDisplayName() // Show guest or username
    val showConfirmDialog = remember { mutableStateOf(false) }

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(scaffoldState.snackbarHostState) }
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

            // Background image
            Image(
                painter = painterResource(Res.drawable.Login2),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Header bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .background(Color(0xFF222937))
                        .border(BorderStroke(2.dp, Color(0xFF4CE3E8)))
                ) {
                    // Left user icon
                    IconButton(
                        onClick = { controller.navigateTo("profile") },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                            .size(55.dp)
                            .background(Color(0xFF222937))
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.user),
                            contentDescription = "Usuario"
                        )
                    }

                    // Right: logout + settings buttons
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { showConfirmDialog.value = true },
                            modifier = Modifier
                                .size(55.dp)
                                .background(Color(0xFF222937))
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.logout),
                                contentDescription = "Cerrar sesión"
                            )
                        }

                        IconButton(
                            onClick = { controller.buttonSettings() },
                            modifier = Modifier
                                .size(55.dp)
                                .background(Color(0xFF222937))
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.ajustes),
                                contentDescription = "Settings"
                            )
                        }
                    }

                    // Display greeting with username
                    Text(
                        text = Strings.t("hi") +"$displayName!",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 72.dp)
                    )
                }
                Spacer(modifier = Modifier.height(70.dp))

                Button(
                    onClick = {controller.goLeaderboard()},
                    modifier = Modifier
                        .align(Alignment.End)
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF222937)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.elevation(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFB0BEC5))
                ){
                    Text(Strings.t("leaderboard"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }

                Spacer(modifier = Modifier.height(60.dp))

                Text(
                    text = Strings.t("categories"),
                    fontSize = 40.sp,
                    color = Color.Black,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Category grid
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CategoryItem(Strings.t("memory"),
                            Res.drawable.iconoMem,
                            Color(0xFF4CE3E8),
                            onNavigateToMemory)

                        CategoryItem(Strings.t("logic"),
                            Res.drawable.iconoLog,
                            Color(0xFF4CE3E8),
                            onNavigateToLogic)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CategoryItem(Strings.t("concentration"),
                            Res.drawable.icono_concentracion,
                            Color(0xFF4CE3E8),
                            onNavigateToConcentration)

                        CategoryItem(Strings.t("mental_agility"),
                            Res.drawable.iconoAgi,
                            Color(0xFF4CE3E8),
                            onNavigateToAgility)
                    }
                }

            }

            // Floating logo on top center
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.TopCenter)
                    .absoluteOffset(y = 30.dp)
                    .scale(1.5f)
            )

            // Bottom drag-bar visual
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

            // Logout confirmation dialog
            if (showConfirmDialog.value) {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog.value = false },
                    title = { Text(Strings.t("log_out")) },
                    text = { Text(Strings.t("you_sure")) },
                    confirmButton = {
                        TextButton(onClick = {
                            showConfirmDialog.value = false
                            coroutineScope.launch {
                                brain.alpha.controller.AuthController.signOut()
                                LoginController.clear()
                                scaffoldState.snackbarHostState.showSnackbar(Strings.t("successful"))
                                controller.navigateTo("login")
                            }
                        }) {
                            Text(Strings.t("yes"))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showConfirmDialog.value = false
                        }) {
                            Text(Strings.t("cancel"))
                        }
                    },
                    properties = DialogProperties()
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
            .width(160.dp)
            .height(180.dp)
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

