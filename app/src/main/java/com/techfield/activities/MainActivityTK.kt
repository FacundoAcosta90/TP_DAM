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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.techfield.database.TicketDatabase
import com.techfield.database.TicketEntity
import com.techfield.repository.TicketRepository
import com.techfield.ui.components.TicketCard
import com.techfield.viewmodel.TicketViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
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
    var filtroActual by remember { mutableStateOf("Nuevo") }

    if (mostrarDialogo) {
        NuevoTicketDialog(
            viewModel = viewModel,
            onDismiss = { mostrarDialogo = false }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF7F7F7),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogo = true },
                containerColor = Color(0xFF6C00FF)
            ) {
                Text("+", color = Color.White, style = MaterialTheme.typography.titleLarge)
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
                text = "Gestione sus tareas de campo asignadas y supervise el progreso en tiempo real.",
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ALINEACIÓN VISUAL: Metemos los 3 botones juntos dentro del Row
            // --- REEMPLAZÁ TU FILA DE BOTONES POR ESTA ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp) // Reducimos un toque el espacio entre botones para ganar ancho
            ) {
                Button(
                    onClick = { filtroActual = "Nuevo" },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp), // Aumentamos la altura del botón para que tenga más presencia
                    contentPadding = PaddingValues(horizontal = 4.dp), // Reduce el margen interno para que entre el texto
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (filtroActual == "Nuevo") Color(0xFF6C00FF) else Color(0xFFE0E0E0),
                        contentColor = if (filtroActual == "Nuevo") Color.White else Color.Black
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp) // Bordes un poco más rectos y modernos
                ) {
                    Text(
                        text = "NUEVO",
                        fontSize = 11.sp, // Achicamos un punto para asegurar que no se corte en pantallas chicas
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }

                Button(
                    onClick = { filtroActual = "En Curso" },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (filtroActual == "En Curso") Color(0xFF6C00FF) else Color(0xFFE0E0E0),
                        contentColor = if (filtroActual == "En Curso") Color.White else Color.Black
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "EN CURSO",
                        fontSize = 11.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }

                Button(
                    onClick = { filtroActual = "Pendiente" },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (filtroActual == "Pendiente") Color(0xFF6C00FF) else Color(0xFFE0E0E0),
                        contentColor = if (filtroActual == "Pendiente") Color.White else Color.Black
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "PENDIENTES",
                        fontSize = 11.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Traemos los tickets del State Flow
            val tickets by viewModel.tickets.collectAsState(initial = emptyList())

            // 2. LÓGICA DE FILTRADO CORREGIDA:
            // Compara directamente el estado del ticket con la pestaña seleccionada
            val ticketsFiltrados = tickets.filter {
                it.estado.equals(filtroActual, ignoreCase = true)
            }

            if (ticketsFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
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
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var prioridad by remember { mutableStateOf("Media") }
    var expanded by remember { mutableStateOf(false) }

    val esFormularioValido = titulo.isNotBlank() && ubicacion.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Ticket de Campo") },
        text = {
            Column {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título *") },
                    isError = titulo.isBlank() && descripcion.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = ubicacion,
                    onValueChange = { ubicacion = it },
                    label = { Text("Ubicación (Planta / Cliente) *") },
                    placeholder = { Text("Ej: Planta Piso 2 o Cliente Central") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = prioridad,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Prioridad") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(text = { Text("Alta") }, onClick = { prioridad = "Alta"; expanded = false })
                        DropdownMenuItem(text = { Text("Media") }, onClick = { prioridad = "Media"; expanded = false })
                        DropdownMenuItem(text = { Text("Baja") }, onClick = { prioridad = "Baja"; expanded = false })
                    }
                }

                if (!esFormularioValido) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "* Los campos Título y Ubicación son obligatorios.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.agregarTicket(
                        TicketEntity(
                            titulo = titulo,
                            descripcion = descripcion,
                            ubicacion = ubicacion,
                            estado = "Nuevo",
                            prioridad = prioridad
                        )
                    )
                    onDismiss()
                },
                enabled = esFormularioValido
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
            icon = { Text("🎟️") },
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