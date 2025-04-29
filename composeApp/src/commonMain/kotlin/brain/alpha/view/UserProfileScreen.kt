package brain.alpha.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.GameSessionController
import brain.alpha.controller.UserProgressEntry
import brain.alpha.model.UserSession
import brain.alpha.strings.Strings

@Composable
fun UserProfileScreen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {

        val userId = UserSession.userId.value
        var progressList by remember { mutableStateOf<List<UserProgressEntry>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(userId) {
            userId?.let {
                progressList = loadUserProgress(it)
                isLoading = false
            }
        }

        Scaffold(
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
                    .padding(16.dp)
            ) {

                Spacer(modifier = Modifier.height(5.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.End)
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF303030)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.elevation(4.dp)
                ) {
                    Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = Strings.t("your_progress"),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

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
            }
        }
    }
}

suspend fun loadUserProgress(userId: String): List<UserProgressEntry> {
    return GameSessionController.getUserProgress(userId).getOrElse { emptyList() }
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
