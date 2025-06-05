package com.swiftcause.swiftcause_android.ui.screen.payment_options

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PaymentOptionsScreen() {
    val donationOptions = listOf("£5", "£10", "£25", "£50")
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var customAmount by remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Donate Now",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            donationOptions.forEach { amount ->
                OutlinedButton(
                    onClick = {
                        selectedOption = amount
                        customAmount = "" // Clear custom input if a preset is selected
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedOption == amount) Color(0x9046FC3D) else Color.Transparent
                    ),
                    border = BorderStroke(1.dp, Color.Gray)
                ) {
                    Text(
                        text = amount,
                        modifier = Modifier.padding(vertical = 20.dp),
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize
                    )
                }
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 14.dp),
                text = "OR",
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            OutlinedTextField(
                value = customAmount,
                onValueChange = {
                    customAmount = it
                    selectedOption = null // Deselect preset if custom input is typed
                },
                leadingIcon = { Text("£") },
                placeholder = { Text("Enter your amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(

            onClick = {
                val donationAmount = selectedOption ?: "£$customAmount"
                // Handle the donation logic here
                Toast.makeText(context, donationAmount, Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Continue")
        }
    }
}

