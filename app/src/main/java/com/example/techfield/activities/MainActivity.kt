package com.example.techfield.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.techfield.database.TicketDatabase
import com.example.techfield.repository.TicketRepository
import com.techfield.ui.screens.LoginScreen
import com.techfield.viewmodel.TicketViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializamos la base de datos y el repositorio
        val database = TicketDatabase.getDatabase(this)
        val repository = TicketRepository(database.ticketDao())

        // Creamos el ViewModel
        val viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TicketViewModel(repository) as T
                }
            }
        )[TicketViewModel::class.java]

        setContent {
            // Le pasamos el viewModel inyectado a la pantalla de Login
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {

                    val intent = Intent(this@MainActivity, MainActivityTK::class.java)
                    startActivity(intent)
                    finish()
                }
            )
        }
    }
}