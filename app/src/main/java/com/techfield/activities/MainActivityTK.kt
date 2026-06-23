package com.techfield.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.techfield.database.TicketDatabase
import com.techfield.database.TicketEntity
import com.techfield.repository.TicketRepository
import com.techfield.ui.components.TicketCard
import com.techfield.viewmodel.TicketViewModel

class MainActivityTK : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = TicketDatabase.Companion.getDatabase(this)
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
            MaterialTheme {
                MainTicketsContent(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTicketsContent(viewModel: TicketViewModel) {
    val context = LocalContext.current
    var mostrarDialogo by remember { mutableStateOf(false) }


    val usuario by viewModel.usuarioLogueado.collectAsState()
    val esIT = usuario?.especialidad?.equals("IT", ignoreCase = true) == true


    var filtroActual by remember { mutableStateOf(if (esIT) "Nuevo" else "Pendiente") }

    LaunchedEffect(esIT) {
        filtroActual = if (esIT) "Nuevo" else "Pendiente"
    }

    if (mostrarDialogo) {
        NuevoTicketDialog(
            viewModel = viewModel,
            onDismiss = { mostrarDialogo = false }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF7F7F7),
        floatingActionButton = {
            if (esIT) {
                FloatingActionButton(
                    onClick = { mostrarDialogo = true },
                    containerColor = Color(0xFF6C00FF)
                ) {
                    Text("+", color = Color.White, style = MaterialTheme.typography.titleLarge)
                }
            }
        },
        bottomBar = {
            TechFieldBottomBar(context = context)
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Mis Tickets",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (esIT) "Panel de supervisión y alta de dispositivos de transmisión." else "Gestione sus tareas de campo asignadas y supervise el progreso en tiempo real.",
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))


            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (esIT) {
                    // Pestañas  para el usuario IT: NUEVO y FINALIZADOS
                    listOf("Nuevo", "Finalizados").forEach { pestaña ->
                        Button(
                            onClick = { filtroActual = pestaña },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (filtroActual == pestaña) Color(0xFF6C00FF) else Color(0xFFE0E0E0),
                                contentColor = if (filtroActual == pestaña) Color.White else Color.Black
                            ),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        ) {
                            Text(pestaña.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Pestañas de trabajo para el personal Técnico
                    listOf("Pendiente", "En Curso", "Finalizado").forEach { pestaña ->
                        Button(
                            onClick = { filtroActual = pestaña },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (filtroActual == pestaña) Color(0xFF6C00FF) else Color(0xFFE0E0E0),
                                contentColor = if (filtroActual == pestaña) Color.White else Color.Black
                            ),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (pestaña == "Finalizado") "FINALIZADOS" else pestaña.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val tickets by viewModel.tickets.collectAsState(initial = emptyList())

            val ticketsFiltrados = tickets.filter { ticket ->
                val estadoTicket = ticket.estado.uppercase()
                if (esIT) {
                    if (filtroActual == "Nuevo") estadoTicket != "FINALIZADO" else estadoTicket == "FINALIZADO"
                } else {
                    estadoTicket == filtroActual.uppercase()
                }
            }

            if (ticketsFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay tickets en esta sección.", color = Color.Gray)
                }
            } else {
                ticketsFiltrados.forEach { ticket ->
                    TicketCard(
                        ticket = ticket,
                        viewModel = viewModel,
                        onDelete = { viewModel.eliminarTicket(ticket) },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoTicketDialog(
    viewModel: TicketViewModel,
    onDismiss: () -> Unit
) {
    val tituloFijo = "Chromecast"
    val listadoFallas = listOf("No enciende", "Presenta lentitud", "No muestra contenido", "No conecta a la red")

    var fallaSeleccionada by remember { mutableStateOf(listadoFallas[0]) }
    var dropdownExpandido by remember { mutableStateOf(false) }
    var ubicacion by remember { mutableStateOf("") }


    val prioridadCalculada = when (fallaSeleccionada) {
        "No enciende", "No muestra contenido" -> "Alta"
        "No conecta a la red" -> "Media"
        "Presenta lentitud" -> "Baja"
        else -> "Media"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Ticket de Campo") },
        text = {
            Column {
                OutlinedTextField(
                    value = tituloFijo,
                    onValueChange = {},
                    label = { Text("Título") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))


                ExposedDropdownMenuBox(
                    expanded = dropdownExpandido,
                    onExpandedChange = { dropdownExpandido = !dropdownExpandido }
                ) {
                    OutlinedTextField(
                        value = fallaSeleccionada,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Descripción (Falla) *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpandido) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = dropdownExpandido,
                        onDismissRequest = { dropdownExpandido = false }
                    ) {
                        listadoFallas.forEach { falla ->
                            DropdownMenuItem(
                                text = { Text(falla) },
                                onClick = {
                                    fallaSeleccionada = falla
                                    dropdownExpandido = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = ubicacion,
                    onValueChange = { ubicacion = it },
                    label = { Text("Ubicación (Planta / Oficina) *") },
                    placeholder = { Text("Ej: Piso 3 - Sala de Reuniones") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = prioridadCalculada,
                    onValueChange = {},
                    label = { Text("Prioridad Asignada Automáticamente") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.agregarTicket(
                        TicketEntity(
                            titulo = tituloFijo,
                            descripcion = fallaSeleccionada,
                            ubicacion = ubicacion,
                            estado = "Pendiente",
                            prioridad = prioridadCalculada
                        )
                    )
                    onDismiss()
                },
                enabled = ubicacion.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun TechFieldBottomBar(context: Context) {
    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Text("🎫") },
            label = { Text("Tickets") }
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                val intent = Intent(context, ProfileActivity::class.java)
                context.startActivity(intent)
            },
            icon = { Text("👤") },
            label = { Text("Perfil") }
        )
    }
}