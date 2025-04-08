package brain.alpha.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.LoginController
import brain.alpha.controller.SettingsController
import brainalpha.composeapp.generated.resources.Res
import brain.alpha.strings.Strings
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(controller: SettingsController) {
    Box(modifier = Modifier.fillMaxSize()
    ){
        val selectedLanguage = if (Strings.currentLanguage == "en") "English" else "Español"


        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(Strings.t("settings_title"), color = Color.White)
                        }
                    },
                    backgroundColor = Color(0xFF303030)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(5.dp)) // Espacio tras el logo

                Button(
                    onClick = {controller.goHome()},
                    modifier = Modifier
                        .align(Alignment.End)
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF303030)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.elevation(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFB0BEC5))
                ){
                    Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
                // PREFERENCIAS
                Text(
                    text = Strings.t("preferences"),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(Strings.t("language"), modifier = Modifier.weight(1f))
                    DropdownMenuLanguage(selectedLanguage) { selected ->
                        Strings.setLanguage(if (selected == "English") "en" else "es")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // CUENTA Y PERFIL
                Text(
                    text = Strings.t("account_profile"),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                SettingsItem(Strings.t("change_name")) {
                    // TODO
                }

                SettingsItem(Strings.t("change_email")) {
                    // TODO
                }

                SettingsItem(Strings.t("change_password")) {
                    // TODO
                }
            }
        }
    }
}
@Composable
fun DropdownMenuLanguage(current: String, onLanguageChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Español", "English")

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(current, color = Color(0xFF0647D7))
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { lang ->
                DropdownMenuItem(onClick = {
                    onLanguageChange(lang)
                    expanded = false
                }) {
                    Text(lang)
                }
            }
        }
    }
}

@Composable
fun SettingsItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = Color(0xFF0647D7)
        )
    }
}
