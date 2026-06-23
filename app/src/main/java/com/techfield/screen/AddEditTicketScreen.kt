package com.techfield.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.techfield.database.TicketEntity
import com.techfield.viewmodel.TicketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTicketScreen(
    navController: NavController,
    viewModel: TicketViewModel
) {
    // El título queda fijo según el requerimiento
    val tituloFijo = "Chromecast"

    // Lista de fallas para el menú desplegable
    val listadoFallas = listOf("No enciende", "Presenta lentitud", "No muestra contenido", "No conecta a la red")

    var fayaSeleccionada by remember { mutableStateOf(listadoFallas[0]) }
    var menuDesplegableExpandido by remember { mutableStateOf(false) }
    var ubicacion by remember { mutableStateOf("") }

    // Lógica condicional automática para la prioridad según la falla seleccionada
    val prioridadAutomatica = when (fayaSeleccionada) {
        "No enciende", "No muestra contenido" -> "Alta"
        "No conecta a la red" -> "Media"
        "Presenta lentitud" -> "Baja"
        else -> "Media"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Campo de Título (Deshabilitado/Lectura para que quede fijo)
        OutlinedTextField(
            value = tituloFijo,
            onValueChange = {},
            label = { Text("Título") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Menú Desplegable Oficial Material 3 para las Fallas (Descripción)
        ExposedDropdownMenuBox(
            expanded = menuDesplegableExpandido,
            onExpandedChange = { menuDesplegableExpandido = !menuDesplegableExpandido }
        ) {
            OutlinedTextField(
                value = fayaSeleccionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Descripción (Falla)") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuDesplegableExpandido) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = menuDesplegableExpandido,
                onDismissRequest = { menuDesplegableExpandido = false }
            ) {
                listadoFallas.forEach { falla ->
                    DropdownMenuItem(
                        text = { Text(falla) },
                        onClick = {
                            fayaSeleccionada = falla
                            menuDesplegableExpandido = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Campo de Ubicación (Ingreso manual)
        OutlinedTextField(
            value = ubicacion,
            onValueChange = { ubicacion = it },
            label = { Text("Ubicación") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo de Prioridad Informativo (Muestra el cálculo automático en tiempo real)
        OutlinedTextField(
            value = prioridadAutomatica,
            onValueChange = {},
            label = { Text("Prioridad Asignada automáticamente") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Guardar
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = ubicacion.isNotBlank(), // Evita guardar si la ubicación está vacía
            onClick = {
                val ahora = System.currentTimeMillis()

                val ticket = TicketEntity(
                    titulo = tituloFijo,
                    descripcion = fayaSeleccionada,
                    ubicacion = ubicacion,
                    estado = "Pendiente",
                    prioridad = prioridadAutomatica,

                    fechaCreacion = ahora,
                    ultimaVezPausado = ahora,
                    tiempoPausadoAcumulado = 0L
                )

                viewModel.agregarTicket(ticket)
                navController.popBackStack()
            }
        ) {
            Text("Guardar Ticket")
        }
    }
}