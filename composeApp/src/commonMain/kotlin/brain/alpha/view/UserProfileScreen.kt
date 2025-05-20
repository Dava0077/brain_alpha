package brain.alpha.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.AuthController
import brain.alpha.controller.GameSessionController
import brain.alpha.controller.UserProgressEntry
import brain.alpha.model.UserSession
import brain.alpha.services.SupabaseClientProvider
import brain.alpha.strings.Strings
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.text.style.TextOverflow
import brainalpha.composeapp.generated.resources.Res
import brainalpha.composeapp.generated.resources.*
import brainalpha.composeapp.generated.resources.iconoInt
import brainalpha.composeapp.generated.resources.user
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/*
 * UserProfileScreen.kt
 *
 * Esta pantalla permite al usuario ver su perfil (nombre, email), progreso en juegos,
 * modificar sus datos (nombre, email, password), cerrar sesión y eliminar la cuenta.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserProfileScreen(onBack: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    var showLogoutConfirm by remember { mutableStateOf(false) }


    val userId = UserSession.userId.value
    val username = UserSession.username.value ?: "Usuario"
    val email = UserSession.email.value ?: "Email desconocido"

    var isEmailVerified by remember { mutableStateOf(true) }
    var progressList by remember { mutableStateOf<List<UserProgressEntry>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var currentPassword by remember { mutableStateOf(TextFieldValue()) }
    var showDialog by remember { mutableStateOf<String?>(null) }
    var inputValue by remember { mutableStateOf(TextFieldValue()) }

    //var showDeleteDialog by remember { mutableStateOf(false) }
    var passwordValue by remember { mutableStateOf(TextFieldValue()) }
    val totalProgress = remember { mutableStateOf<List<UserProgressEntry>>(emptyList()) }
    val scrollState = rememberScrollState()

    // Carga de progreso del usuario al iniciar
    LaunchedEffect(userId) {
        userId?.let {
            val result = GameSessionController.getAllTotalScores(it)
            result.onSuccess {
                println("✅ TotalProgress: $it")
                totalProgress.value = it
            }.onFailure {
                println("❌ Error al cargar progreso total")
            }
        }
    }


    // Outer container
    Box(modifier = Modifier.fillMaxSize()) {
        /*LaunchedEffect(userId) {
            userId?.let {
                val progress = loadUserProgress(it)
                println("🧠 Progreso cargado: $progress")  // ← DEBUG
                progressList = progress
                isLoading = false
            }
        }*/

        // Scaffold with top app bar
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
            // Main vertical scrollable content
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
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

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showLogoutConfirm = true },
                        modifier = Modifier
                            .height(40.dp)
                            .defaultMinSize(minWidth = 100.dp)
                            .wrapContentWidth(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.elevation(4.dp)
                    ) {
                        Text(
                            text = Strings.t("log_out"),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val userId = UserSession.userId.value ?: return@launch
                                val success = AuthController.deleteUserFromAuth(userId)

                                if (success) {
                                    scaffoldState.snackbarHostState.showSnackbar("Cuenta eliminada correctamente")
                                    AuthController.signOut()
                                } else {
                                    scaffoldState.snackbarHostState.showSnackbar("Error al eliminar cuenta")
                                }
                            }
                        },
                        modifier = Modifier
                            .height(40.dp)
                            .defaultMinSize(minWidth = 100.dp)
                            .wrapContentWidth(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF303030)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = Strings.t("delete_account"),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .height(40.dp)
                            .defaultMinSize(minWidth = 100.dp)
                            .wrapContentWidth(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF303030)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.elevation(4.dp)
                    ) {
                        Text(
                            text = Strings.t("back"),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (!isEmailVerified) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val result = AuthController.resendVerification(email)
                                val msg = if (result.isSuccess) "Verificación reenviada" else "Error al reenviar"
                                scaffoldState.snackbarHostState.showSnackbar(msg)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7))
                    ) {
                        Text("Reenviar verificación", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionButton(Strings.t("change_name")) { showDialog = "name" }
                    ActionButton(Strings.t("change_email")) { showDialog = "email" }
                    ActionButton(Strings.t("change_password")) { showDialog = "password" }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = Strings.t("your_progress"),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        horizontalArrangement = Arrangement.End, // 🔥 Esto alinea a la derecha
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DifficultyLegendItem(Res.drawable.easy, Strings.t("easy"),  size = 40.dp)
                        DifficultyLegendItem(Res.drawable.medium, Strings.t("medium"), size = 50.dp)
                        DifficultyLegendItem(Res.drawable.hard, Strings.t("hard"), size = 60.dp)
                    }
                }




                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Strings.t("game"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = Strings.t("neurons2"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = Strings.t("difficulty"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }

                totalProgress.value.forEach { entry ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = 4.dp,
                        backgroundColor = Color(0xFFF2F2F2)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Nombre del juego (izquierda)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.iconoInt),
                                    contentDescription = null,
                                    tint = Color(0xFF64B5F6),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = Strings.t(entry.gameName),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )
                            }

                            // Score en medio
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "${entry.score} ",
                                    fontSize = 18.sp,
                                    color = Color.DarkGray
                                )
                            }

                            // Dificultad (derecha)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.weight(1f)
                            ) {
                                val difficultyLabel = when (entry.difficulty) {
                                    "easy" -> Strings.t("easy")
                                    "medium" -> Strings.t("medium")
                                    "hard" -> Strings.t("hard")
                                    else -> "?"
                                }
                                val difficultyIcon = when (entry.difficulty) {
                                    "easy" -> Res.drawable.easy
                                    "medium" -> Res.drawable.medium
                                    "hard" -> Res.drawable.hard
                                    else -> Res.drawable.iconoInt
                                }

                                Image(
                                    painter = painterResource(difficultyIcon),
                                    contentDescription = "Dificultad: $difficultyLabel",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                    }

                }


                Spacer(modifier = Modifier.height(16.dp))

                Spacer(modifier = Modifier.height(12.dp))



//                //Delete button
//                Button(
//                    onClick = { showDeleteDialog = true },
//                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray),
//                    modifier = Modifier.align(Alignment.CenterHorizontally)
//                ) {
//                    Text("Eliminar cuenta", color = Color.White)
//                }
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

        //Delete dialog
//        if (showDeleteDialog) {
//            AlertDialog(
//                onDismissRequest = { showDeleteDialog = false },
//                title = { Text("Eliminar cuenta") },
//                text = { Text("¿Estás seguro? Esta acción no se puede deshacer.") },
//                confirmButton = {
//                    TextButton(onClick = {
//                        coroutineScope.launch {
//                            val result = AuthController.deleteAccount()
//                            if (result.isSuccess) {
//                                scaffoldState.snackbarHostState.showSnackbar("Cuenta eliminada")
//                                onBack()
//                            } else {
//                                scaffoldState.snackbarHostState.showSnackbar("Error al eliminar cuenta")
//                            }
//                        }
//                        showDeleteDialog = false
//                    }) {
//                        Text("Sí, eliminar")
//                    }
//                },
//                dismissButton = {
//                    TextButton(onClick = { showDeleteDialog = false }) {
//                        Text("Cancelar")
//                    }
//                }
//            )
//        }

        showDialog?.let { field ->
            AlertDialog(
                onDismissRequest = {
                    showDialog = null
                    inputValue = TextFieldValue()
                    currentPassword = TextFieldValue()
                },
                title = {
                    Text(Strings.t("change_$field"))
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = inputValue,
                            onValueChange = { inputValue = it },
                            label = { Text(Strings.t("new_$field")) },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                        )
                        if (field == "email" || field == "password") {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = passwordValue,
                                onValueChange = { passwordValue = it },
                                label = { Text(Strings.t("current_password")) },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            if (inputValue.text.isBlank()) {
                                scaffoldState.snackbarHostState.showSnackbar(Strings.t("field_cannot_be_empty"))
                                return@launch
                            }

                            if (field == "email" || field == "password") {
                                val email = UserSession.email.value ?: return@launch
                                val result = AuthController.verifyCurrentPassword(email, passwordValue.text)
                                if (result.isFailure) {
                                    scaffoldState.snackbarHostState.showSnackbar(Strings.t("invalid_password"))
                                    return@launch
                                }
                            }

                            updateUserField(field, inputValue.text)
                            scaffoldState.snackbarHostState.showSnackbar(Strings.t("update_successful"))
                            showDialog = null
                            inputValue = TextFieldValue()
                            currentPassword = TextFieldValue()
                        }
                    }) {
                        Text(Strings.t("confirm"))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDialog = null
                        inputValue = TextFieldValue()
                        currentPassword = TextFieldValue()
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
        try {
            when (field) {
                "name" -> {
                    client.postgrest["users"].update(
                        buildJsonObject { put("name", value) }
                    ) { filter { eq("id", userId) } }

                    UserSession.username.value = value
                }

                "email" -> {
                    client.auth.updateUser { email = value }

                    client.postgrest["users"].update(
                        buildJsonObject { put("email", value) }
                    ) { filter { eq("id", userId) } }

                    UserSession.email.value = value
                }

                "password" -> {
                    client.auth.updateUser { password = value }
                }

                else -> println("⚠️ Campo no soportado: $field")
            }
        } catch (e: Exception) {
            println("❌ Error al actualizar campo '$field': ${e.message}")
        }
    }
}




suspend fun verifyCurrentPassword(password: String): Boolean {
    val email = UserSession.email.value ?: return false
    return runCatching {
        SupabaseClientProvider.client.auth.signInWith(io.github.jan.supabase.auth.providers.builtin.Email) {
            this.email = email
            this.password = password
        }
    }.isSuccess
}

/*suspend fun loadUserProgress(userId: String): List<UserProgressEntry> {
    return GameSessionController.getUserProgress(userId).getOrElse { emptyList() }
}*/

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
    println("📄 Mostrando progreso: $progress")
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
@Composable
fun DifficultyLegendItem(image: DrawableResource, label: String, size: Dp) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(image),
            contentDescription = label,
            modifier = Modifier.size(size)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            style = TextStyle(Color.Black), fontWeight = FontWeight.Bold

        )
    }
}


