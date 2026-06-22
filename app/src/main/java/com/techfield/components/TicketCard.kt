package com.techfield.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.techfield.database.TicketEntity
import com.techfield.viewmodel.TicketViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketCard(
    ticket: TicketEntity,
    viewModel: TicketViewModel,
    onDelete: () -> Unit,
    onUpdateTicket: (TicketEntity) -> Unit
) {
    val contexto = androidx.compose.ui.platform.LocalContext.current
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    var verDetalle by remember { mutableStateOf(false) }

    val listaComentarios by viewModel.obtenerComentarios(ticket.id).collectAsState(initial = emptyList())
    var nuevoComentarioTexto by remember { mutableStateOf("") }

    var bitmapFoto by remember(ticket.fotoUri) {
        mutableStateOf<android.graphics.Bitmap?>(null)
    }

    LaunchedEffect(ticket.fotoUri) {
        if (!ticket.fotoUri.isNullOrEmpty()) {
            try {
                val uri = android.net.Uri.parse(ticket.fotoUri)
                if (android.os.Build.VERSION.SDK_INT < 28) {
                    bitmapFoto = android.provider.MediaStore.Images.Media.getBitmap(contexto.contentResolver, uri)
                } else {
                    val source = android.graphics.ImageDecoder.createSource(contexto.contentResolver, uri)
                    bitmapFoto = android.graphics.ImageDecoder.decodeBitmap(source)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            onUpdateTicket(ticket.copy(fotoUri = uri.toString()))
        }
    }

    val launcherCamara = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview()
    ) { bitmap: android.graphics.Bitmap? ->
        if (bitmap != null) {
            bitmapFoto = bitmap
        }
    }

    val launcherPermiso = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { aprobado ->
        if (aprobado) {
            launcherCamara.launch(null)
        } else {
            android.widget.Toast.makeText(contexto, "Permiso de cámara denegado", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    // ======================================================================
    // LOGICA DEL SLA - CONSERVADA DE TU IMPLEMENTACIÓN
    // ======================================================================
    val tiempoLimiteSLA = when (ticket.prioridad.uppercase()) {
        "ALTA" -> 2 * 60 * 60 * 1000L      // 2 Horas
        "MEDIA" -> 8 * 60 * 60 * 1000L     // 8 Horas
        "BAJA" -> 24 * 60 * 60 * 1000L     // 24 Horas
        else -> 8 * 60 * 60 * 1000L
    }

    var tiempoTranscurridoTexto by remember { mutableStateOf("00:00:00") }
    var estaVencido by remember { mutableStateOf(false) }

    LaunchedEffect(ticket.estado, ticket.tiempoPausadoAcumulado, ticket.ultimaVezPausado) {
        if (ticket.estado.uppercase() == "FINALIZADO") {
            tiempoTranscurridoTexto = "Ticket Terminado"
            estaVencido = false
            return@LaunchedEffect
        }

        while (true) {
            val ahora = System.currentTimeMillis()
            val tiempoFinalParaCalculo = ticket.ultimaVezPausado ?: ahora
            var tiempoActivoNeto = (tiempoFinalParaCalculo - ticket.fechaCreacion) - ticket.tiempoPausadoAcumulado
            if (tiempoActivoNeto < 0) tiempoActivoNeto = 0

            estaVencido = tiempoActivoNeto > tiempoLimiteSLA

            val segundosTotales = tiempoActivoNeto / 1000
            val horas = segundosTotales / 3600
            val minutos = (segundosTotales % 3600) / 60
            val segundos = segundosTotales % 60
            tiempoTranscurridoTexto = String.format("%02d:%02d:%02d", horas, minutos, segundos)

            kotlinx.coroutines.delay(1000L)
        }
    }
    // ======================================================================

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = { Text("¿Finalizar Ticket?") },
            text = { Text("Al marcar este ticket como 'Finalizado', se registrará en Tickets Finalizados.") },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarConfirmacion = false
                        onUpdateTicket(ticket.copy(estado = "Finalizado"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Sí, finalizar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacion = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (verDetalle) {
        AlertDialog(
            onDismissRequest = { verDetalle = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier.fillMaxSize()
        ) {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
                    TextButton(onClick = { verDetalle = false }) { Text("← Volver a Mis Tickets") }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "#TK-${ticket.id}", color = Color(0xFF6C00FF), style = MaterialTheme.typography.labelLarge)
                    Text(text = ticket.titulo, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "📍 Ubicación: ${ticket.ubicacion}", color = Color.Gray)
                    Text(text = "⚡ Estado actual: ${ticket.estado}", color = Color.Gray)

                    Text(text = "⏳ SLA Activo: $tiempoTranscurridoTexto", color = Color.Gray)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

                    Text(text = "Evidencias fotográficas", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Selecciona una foto de la galería o toma una nueva con la cámara.", color = Color.Gray)
                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                            if (bitmapFoto != null) {
                                Image(
                                    bitmap = bitmapFoto!!.asImageBitmap(),
                                    contentDescription = "Evidencia",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                                Row(
                                    modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter).padding(bottom = 12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(onClick = { launcherPermiso.launch(android.Manifest.permission.CAMERA) }, colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.7f))) {
                                        Text("📸 Cámara")
                                    }
                                    Button(onClick = { launcherGaleria.launch("image/*") }, colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.7f))) {
                                        Text("🖼 Galería")
                                    }
                                }
                            } else {
                                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                                    Text("📷", style = MaterialTheme.typography.headlineLarge)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Button(onClick = { launcherPermiso.launch(android.Manifest.permission.CAMERA) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C00FF))) {
                                            Text("Usar Cámara")
                                        }
                                        Button(onClick = { launcherGaleria.launch("image/*") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C00FF))) {
                                            Text("Abrir Galería")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

                    Text(text = "📜 Bitácora de Notas", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = nuevoComentarioTexto,
                            onValueChange = { nuevoComentarioTexto = it },
                            placeholder = { Text("Escribe un avance o nota...") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (nuevoComentarioTexto.isNotBlank()) {
                                    viewModel.agregarComentario(ticket.id, nuevoComentarioTexto)
                                    nuevoComentarioTexto = ""
                                }
                            }
                        ) {
                            Text("Añadir")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (listaComentarios.isEmpty()) {
                        Text(
                            text = "No hay notas en la bitácora todavía.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            listaComentarios.forEach { comentario ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = "🔧 Técnico", style = MaterialTheme.typography.labelMedium, color = Color(0xFF6C00FF))
                                            Text(text = comentario.fechaHora, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = comentario.texto, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    Card(
        onClick = { verDetalle = true },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "#TK-${ticket.id}", color = Color(0xFF6C00FF))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = ticket.titulo, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "📍 ${ticket.ubicacion}", color = Color.Gray)
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "⚡ Prioridad: ${ticket.prioridad}",
                color = when (ticket.prioridad) {
                    "Alta" -> Color.Red
                    "Media" -> Color(0xFFFF9800)
                    else -> Color(0xFF4CAF50)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⏳ SLA: $tiempoTranscurridoTexto",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
                if (ticket.estado.uppercase() != "FINALIZADO") {
                    Text(
                        text = if (estaVencido) "⚠️ VENCIDO" else "✅ A TIEMPO",
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (estaVencido) Color.Red else Color(0xFF4CAF50)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                var menuExpandido by remember { mutableStateOf(false) }
                val estadosDisponibles = listOf("Pendiente", "En Curso", "Finalizado")

                ExposedDropdownMenuBox(
                    expanded = menuExpandido,
                    onExpandedChange = { menuExpandido = !menuExpandido }
                ) {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                text = "${ticket.estado} ▾",
                                color = when (ticket.estado.uppercase()) {
                                    "PENDIENTE" -> Color(0xFFFF9800)
                                    "EN CURSO" -> Color(0xFF2196F3)
                                    "FINALIZADO" -> Color(0xFF4CAF50)
                                    else -> Color.Gray
                                }
                            )
                        },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(expanded = menuExpandido, onDismissRequest = { menuExpandido = false }) {
                        estadosDisponibles.forEach { opcionEstado ->
                            DropdownMenuItem(
                                text = { Text(opcionEstado) },
                                onClick = {
                                    menuExpandido = false
                                    if (opcionEstado == "Finalizado") {
                                        mostrarConfirmacion = true
                                    } else {
                                        val ahora = System.currentTimeMillis()

                                        val ticketActualizado = when {
                                            opcionEstado.equals("Pendiente", ignoreCase = true) -> {
                                                ticket.copy(
                                                    estado = opcionEstado,
                                                    ultimaVezPausado = ahora
                                                )
                                            }
                                            ticket.estado.equals("Pendiente", ignoreCase = true) && ticket.ultimaVezPausado != null -> {
                                                val tiempoEnEstaPausa = ahora - ticket.ultimaVezPausado
                                                ticket.copy(
                                                    estado = opcionEstado,
                                                    ultimaVezPausado = null,
                                                    tiempoPausadoAcumulado = ticket.tiempoPausadoAcumulado + tiempoEnEstaPausa
                                                )
                                            }
                                            else -> {
                                                ticket.copy(estado = opcionEstado)
                                            }
                                        }

                                        onUpdateTicket(ticketActualizado)
                                    }
                                }
                            )
                        }
                    }
                }

                OutlinedButton(onClick = { onDelete() }) { Text("🗑 Eliminar") }
            }
        }
    }
}