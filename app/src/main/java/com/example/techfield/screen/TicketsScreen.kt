package com.techfield.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.techfield.ui.components.TicketCard
import com.techfield.viewmodel.TicketViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp

@Composable
fun TicketsScreen(
    navController: NavController,
    viewModel: TicketViewModel
) {

    val tickets = viewModel.tickets.collectAsState().value

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("addTicket")
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar"
                )
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            items(tickets) { ticket ->

                TicketCard(
                    ticket = ticket,
                    viewModel = viewModel,
                    onDelete = {
                        viewModel.eliminarTicket(ticket)
                    },
                    onUpdateTicket = { ticketEditado ->
                        viewModel.actualizarTicket(ticketEditado)
                    }
                )


                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}