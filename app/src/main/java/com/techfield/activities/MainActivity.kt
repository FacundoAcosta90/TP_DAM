package com.techfield.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.techfield.database.TicketDatabase
import com.techfield.repository.TicketRepository
import com.techfield.ui.screens.LoginScreen
import com.techfield.viewmodel.TicketViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val database = TicketDatabase.getDatabase(this)
        val repository = TicketRepository(database.ticketDao())


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