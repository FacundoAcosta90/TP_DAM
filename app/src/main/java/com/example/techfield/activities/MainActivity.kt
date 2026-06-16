package com.example.techfield.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.techfield.database.TicketDatabase
import com.example.techfield.repository.TicketRepository
import com.techfield.ui.screens.LoginScreen // Asegurate de que el import sea correcto
import com.techfield.viewmodel.TicketViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicializamos la base de datos y el repositorio
        val database = TicketDatabase.getDatabase(this)
        val repository = TicketRepository(database.ticketDao())

        // 2. Creamos el ViewModel (Igual a como lo hacés en las otras pantallas)
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
            // 3. Le pasamos el viewModel inyectado a la pantalla de Login
            LoginScreen(
                viewModel = viewModel, // <-- ACÁ CORREGIMOS EL ERROR ROJO
                onLoginSuccess = {
                    // Flujo de éxito: Redirige a la lista de tickets
                    val intent = Intent(this@MainActivity, MainActivityTK::class.java)
                    startActivity(intent)
                    finish()
                }
            )
        }
    }
}