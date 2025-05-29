package com.swiftcause.swiftcause_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.swiftcause.swiftcause_android.screens.WelcomeScreen
import com.swiftcause.swiftcause_android.ui.theme.SwiftCause_androidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SwiftCause_androidTheme {
                WelcomeScreen()
            }
        }
    }
}






