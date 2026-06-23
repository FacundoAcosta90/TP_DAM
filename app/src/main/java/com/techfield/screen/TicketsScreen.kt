package com.techfield.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    val todosLosTickets = viewModel.tickets.collectAsState().value
    val usuario = viewModel.usuarioLogueado.collectAsState().value

    val esIT = usuario?.especialidad?.equals("IT", ignoreCase = true) == true

    var seccionSeleccionada by remember { mutableStateOf(0) }
    val pestañas = if (esIT) {
        listOf("Activos", "Finalizados")
    } else {
        listOf("Pendientes", "En Curso", "Finalizados")
    }


    LaunchedEffect(usuario) {
        seccionSeleccionada = 0
    }


    val ticketsFiltrados = todosLosTickets.filter { ticket ->
        val estadoTicket = ticket.estado.uppercase()
        if (esIT) {
            if (seccionSeleccionada == 0) {
                estadoTicket != "FINALIZADO"
            } else {
                estadoTicket == "FINALIZADO"
            }
        } else {
            when (seccionSeleccionada) {
                0 -> estadoTicket == "PENDIENTE"
                1 -> estadoTicket == "EN CURSO"
                else -> estadoTicket == "FINALIZADO"
            }
        }
    }

    Scaffold(

        floatingActionButton = {
            if (esIT) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("addTicket")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar Ticket"
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            TabRow(selectedTabIndex = seccionSeleccionada) {
                pestañas.forEachIndexed { indice, titulo ->
                    Tab(
                        selected = seccionSeleccionada == indice,
                        onClick = { seccionSeleccionada = indice },
                        text = { Text(titulo) }
                    )
                }
            }


            if (ticketsFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay tickets en esta sección.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(ticketsFiltrados) { ticket ->
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
}