package com.techfield.ui.screens

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techfield.database.TicketEntity
import com.techfield.database.RepuestoEntity
import com.techfield.viewmodel.TicketViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    ticket: TicketEntity,
    viewModel: TicketViewModel,
    onBack: () -> Unit
) {
    val contexto = LocalContext.current
    val listaRepuestosDisponible by viewModel.obtenerRepuestos().collectAsState(initial = emptyList())

    var notaObservacion by remember { mutableStateOf(ticket.observacionesTecnicas) }
    var notaCierreTarea by remember { mutableStateOf("") }
    var repuestoSeleccionado by remember { mutableStateOf<RepuestoEntity?>(null) }
    var menuRepuestosExpandido by remember { mutableStateOf(false) }


    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.actualizarTicket(ticket.copy(fotoUri = uri.toString()))
            Toast.makeText(contexto, "Evidencia cargada desde galería", Toast.LENGTH_SHORT).show()
        }
    }

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            Toast.makeText(contexto, "Foto capturada con éxito", Toast.LENGTH_SHORT).show()
        }
    }

    val launcherPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { aprobado ->
        if (aprobado) {
            launcherCamara.launch(null)
        } else {
            Toast.makeText(contexto, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "#TK-${ticket.id}", color = Color(0xFF6C00FF), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Text(text = ticket.titulo, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "📍 Ubicación: ${ticket.ubicacion}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(text = "⚡ Detalle de Falla: ${ticket.descripcion}", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color(0xFFE0E0E0))
        Spacer(modifier = Modifier.height(16.dp))

        when (ticket.estado.uppercase()) {
            "PENDIENTE" -> {
                Text(text = "📋 El ticket se encuentra asignado pero no iniciado.", color = Color.Gray)
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val ahora = System.currentTimeMillis()
                        val fechaFormateada = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(ahora))
                        val ticketIniciado = ticket.copy(
                            estado = "En Curso",
                            timestampInicio = ahora,
                            fechaInicioFormat = fechaFormateada
                        )
                        viewModel.actualizarTicket(ticketIniciado)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text("Iniciar Tarea (Activar SLA)", fontWeight = FontWeight.Bold)
                }
            }

            "EN CURSO" -> {
                Text(text = "⏱️ Hora de Inicio: ${ticket.fechaInicioFormat ?: "No registrada"}", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))

                val slaVencido = viewModel.calcularVencimientoSLA(ticket.timestampInicio, limiteHoras = 72)
                if (slaVencido) {
                    Text(text = "🚨 SLA: VENCIDO", color = Color.Red, fontWeight = FontWeight.Bold)
                } else {
                    val cuentaRegresiva = viewModel.obtenerCuentaRegresivaSLA(ticket.timestampInicio)
                    Text(text = "⏳ Tiempo Restante SLA: $cuentaRegresiva", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = notaObservacion,
                    onValueChange = { notaObservacion = it },
                    label = { Text("Bitácora de Observaciones en Campo") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val ticketActualizado = ticket.copy(
                            estado = "Finalizado",
                            observacionesTecnicas = notaObservacion
                        )
                        viewModel.actualizarTicket(ticketActualizado)
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C00FF))
                ) {
                    Text("Finalizar Tarea", fontWeight = FontWeight.Bold)
                }
            }

            "FINALIZADO" -> {
                Text(text = "📝 Historial de Bitácora (En Curso):", fontWeight = FontWeight.Bold)
                Text(
                    text = ticket.observacionesTecnicas.ifBlank { "Sin anotaciones previas en campo." },
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = notaCierreTarea,
                    onValueChange = { notaCierreTarea = it },
                    label = { Text("Detalle de Tareas Realizadas (Cierre)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (notaCierreTarea.isNotBlank()) {
                            viewModel.obtenerComentarios(ticket.id)
                            viewModel.agregarComentario(ticket.id, "Cierre de tarea: $notaCierreTarea")
                            notaCierreTarea = ""
                            Toast.makeText(contexto, "Nota de cierre añadida", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = notaCierreTarea.isNotBlank()
                ) {
                    Text("Registrar Avance")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "📸 Evidencias de Cierre de Ticket", fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { launcherPermiso.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Text("Cámara")
                    }
                    Button(
                        onClick = { launcherGaleria.launch("image/*") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Text("Galería")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "🛠️ Menú de Asignación de Repuestos", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = menuRepuestosExpandido,
                    onExpandedChange = { menuRepuestosExpandido = !menuRepuestosExpandido }
                ) {
                    OutlinedTextField(
                        value = repuestoSeleccionado?.nombre ?: "Seleccionar Insumo Utilizado",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuRepuestosExpandido) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = menuRepuestosExpandido,
                        onDismissRequest = { menuRepuestosExpandido = false }
                    ) {
                        listaRepuestosDisponible.forEach { repuesto ->
                            DropdownMenuItem(
                                text = { Text("${repuesto.nombre} (Disponibles: ${repuesto.stock})") },
                                onClick = {
                                    repuestoSeleccionado = repuesto
                                    menuRepuestosExpandido = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        repuestoSeleccionado?.let {
                            if (it.stock > 0) {
                                viewModel.descontarStockRepuesto(it.id, cantidad = 1)
                                Toast.makeText(contexto, "Stock descontado: ${it.nombre}", Toast.LENGTH_SHORT).show()
                                repuestoSeleccionado = null
                            } else {
                                Toast.makeText(contexto, "No hay stock disponible de este insumo", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = repuestoSeleccionado != null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Confirmar Descuento de Stock")
                }
            }
        }
    }
}