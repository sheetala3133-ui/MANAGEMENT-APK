package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.ui.screens.GatePassAppContent
import com.example.ui.viewmodels.GatePassViewModel
import com.example.ui.viewmodels.GatePassViewModelFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize state-driven GatePass ViewModel with database factory
    val factory = GatePassViewModelFactory(application)
    val viewModel = ViewModelProvider(this, factory)[GatePassViewModel::class.java]

    setContent {
      GatePassAppContent(viewModel = viewModel)
    }
  }
}

