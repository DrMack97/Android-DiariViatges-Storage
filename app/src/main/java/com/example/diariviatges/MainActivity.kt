package com.example.diariviatges

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.diariviatges.ui.navigation.AppNavigation
import com.example.diariviatges.ui.theme.DiariViatgesTheme

/** Única Activity de l'app: només carrega el tema i el graf de navegació. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiariViatgesTheme {
                AppNavigation()
            }
        }
    }
}
