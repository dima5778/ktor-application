package com.example.directoryapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.directoryapplication.presentation.navigation.AppNavigation
import com.example.directoryapplication.presentation.theme.DirectoryApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DirectoryApplicationTheme {
                AppNavigation()
            }
        }
    }
}