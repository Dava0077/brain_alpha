package brain.alpha.view.mentalAgilityView

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.mentalAgilityController.clickNumberController
import brain.alpha.model.mentalAgilityModel.clickNumberModel

@Composable
fun GameView() {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Mensaje del juego
        Text(
            text = clickNumberController.message,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Cuadrícula de botones
        for (row in 0 until 4) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (col in 0 until 4) {
                    val index = row * 4 + col
                    val number = clickNumberController.numbers.getOrNull(index) ?: 0
                    Button(
                        onClick = { clickNumberController.handleButtonClick(number) },
                        modifier = Modifier.size(60.dp)
                    ) {
                        Text(text = number.toString())
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}