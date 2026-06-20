package com.techfield.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.techfield.ui.components.TicketCard
import com.techfield.viewmodel.TicketViewModel

@Composable
fun TicketsScreen(
    navController: NavController,
    viewModel: TicketViewModel
) {
    // 1. Traemos todos los tickets de la base de datos
    val todosLosTickets = viewModel.tickets.collectAsState().value

    // 2. Filtramos la lista para quedarnos SOLO con los que NO están finalizados
    val ticketsActivos = todosLosTickets.filter { it.estado.uppercase() != "FINALIZADO" }

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

        // 3. Si la lista filtrada está vacía, mostramos el mensaje de que no hay pendientes
        if (ticketsActivos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes tareas pendientes por el momento.", color = Color.Gray)
            }
        } else {
            // 4. Se eliminó la tarjeta del resumen superior.
            // El LazyColumn ahora usa directamente "ticketsActivos"
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(ticketsActivos) { ticket ->
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
}