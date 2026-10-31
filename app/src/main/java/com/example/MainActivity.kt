package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.container.MainContainer
import com.example.ui.theme.PhishShieldTheme
import com.example.viewmodel.ScannerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PhishShieldTheme {
                val viewModel: ScannerViewModel = viewModel()
                MainContainer(viewModel = viewModel)
            }
        }
    }
}
