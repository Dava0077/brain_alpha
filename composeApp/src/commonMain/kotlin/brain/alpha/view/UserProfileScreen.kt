package brain.alpha.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.AuthController
import brain.alpha.controller.GameSessionController
import brain.alpha.controller.UserProgressEntry
import brain.alpha.model.UserSession
import brain.alpha.services.SupabaseClientProvider
import brain.alpha.strings.Strings
import brainalpha.composeapp.generated.resources.Res
import brainalpha.composeapp.generated.resources.user
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jetbrains.compose.resources.painterResource

@Composable
fun UserProfileScreen(onBack: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    var showLogoutConfirm by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        val userId = UserSession.userId.value
        val username = UserSession.username.value ?: "Usuario"
        val email = UserSession.email.value ?: "Email desconocido"

        var progressList by remember { mutableStateOf<List<UserProgressEntry>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }

        var showDialog by remember { mutableStateOf<String?>(null) }
        var inputValue by remember { mutableStateOf(TextFieldValue()) }

        LaunchedEffect(userId) {
            userId?.let {
                progressList = loadUserProgress(it)
                isLoading = false
            }
        }

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(Strings.t("profile_title"), color = Color.White)
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(10.dp))

                Image(
                    painter = painterResource(Res.drawable.user),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(username, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(email, fontSize = 14.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionButton(Strings.t("change_name")) { showDialog = "name" }
                    ActionButton(Strings.t("change_email")) { showDialog = "email" }
                    ActionButton(Strings.t("change_password")) { showDialog = "password" }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = Strings.t("your_progress"),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (progressList.isEmpty()) {
                    Text(Strings.t("no_progress"), modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(progressList) { progress ->
                            ProgressItem(progress)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF303030)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.elevation(4.dp)
                ) {
                    Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { showLogoutConfirm = true },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.elevation(4.dp)
                ) {
                    Text(Strings.t("log_out"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }
        }

        if (showLogoutConfirm) {
            AlertDialog(
                onDismissRequest = { showLogoutConfirm = false },
                title = { Text(Strings.t("log_out")) },
                text = { Text(Strings.t("you_sure")) },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutConfirm = false
                        coroutineScope.launch {
                            AuthController.signOut()
                            scaffoldState.snackbarHostState.showSnackbar(Strings.t("successful"))
                            onBack()
                        }
                    }) {
                        Text(Strings.t("yes"))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutConfirm = false }) {
                        Text(Strings.t("cancel"))
                    }
                }
            )
        }

        showDialog?.let { field ->
            AlertDialog(
                onDismissRequest = {
                    showDialog = null
                    inputValue = TextFieldValue()
                },
                title = {
                    Text(Strings.t("change_$field"))
                },
                text = {
                    OutlinedTextField(
                        value = inputValue,
                        onValueChange = { inputValue = it },
                        label = { Text(Strings.t("new_$field")) },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (inputValue.text.isNotBlank()) {
                            coroutineScope.launch {
                                updateUserField(field, inputValue.text)
                                scaffoldState.snackbarHostState.showSnackbar(Strings.t("update_successful"))
                            }
                        } else {
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(Strings.t("field_cannot_be_empty"))
                            }
                        }
                        showDialog = null
                        inputValue = TextFieldValue()
                    }) {
                        Text(Strings.t("confirm"))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDialog = null
                        inputValue = TextFieldValue()
                    }) {
                        Text(Strings.t("cancel"))
                    }
                }
            )
        }
    }
}

suspend fun updateUserField(field: String, value: String) {
    val client = SupabaseClientProvider.client
    val userId = UserSession.userId.value ?: return

    withContext(Dispatchers.Default) {
        when (field) {
            "name" -> {
                client.postgrest["users"].update(
                    buildJsonObject {
                        put("name", value)
                    }
                ) {
                    filter { eq("id", userId) }
                }
                UserSession.username.value = value
            }
            "email" -> {
                client.auth.updateUser {
                    email = value
                }
                client.postgrest["users"].update(
                    buildJsonObject {
                        put("email", value)
                    }
                ) {
                    filter { eq("id", userId) }
                }
                UserSession.email.value = value
            }
            "password" -> {
                client.auth.updateUser {
                    password = value
                }
            }
            else -> {}
        }
    }
}

suspend fun loadUserProgress(userId: String): List<UserProgressEntry> {
    return GameSessionController.getUserProgress(userId).getOrElse { emptyList() }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text)
    }
}

@Composable
fun ProgressItem(progress: UserProgressEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 4.dp,
        backgroundColor = Color(0xFFF5F5F5)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = progress.gameName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Score: ${progress.score}", fontSize = 14.sp)
            Text(text = "Last Played: ${progress.lastPlayed}", fontSize = 14.sp)
        }
    }
}