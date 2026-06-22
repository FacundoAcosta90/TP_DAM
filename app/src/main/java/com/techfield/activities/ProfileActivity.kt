package com.techfield.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techfield.database.TicketDatabase
import com.techfield.repository.TicketRepository
import com.techfield.viewmodel.TicketViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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
                ProfileContent(
                    viewModel = viewModel,
                    onCerrarSesion = {
                        viewModel.setUsuarioLogueado(null)
                        val intent = Intent(this@ProfileActivity, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    },
                    onIrATickets = {
                        startActivity(Intent(this@ProfileActivity, MainActivityTK::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    viewModel: TicketViewModel,
    onCerrarSesion: () -> Unit,
    onIrATickets: () -> Unit
) {
    // Escucha al usuario real logueado en el sistema
    val usuarioLogueado by viewModel.usuarioLogueado.collectAsState(initial = null)

    val listaTickets by viewModel.tickets.collectAsState(initial = emptyList())
    val ticketsEnCurso = listaTickets.count { it.estado == "En Curso" }
    val ticketsFinalizados = listaTickets.count { it.estado.equals("Finalizado", ignoreCase = true) }
    val scrollState = rememberScrollState()

    val esIT = usuarioLogueado?.especialidad?.equals("IT", ignoreCase = true) == true

    Scaffold(
        containerColor = Color(0xFFF7F7F7),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = onIrATickets,
                    icon = { Text("🎫") },
                    label = { Text("Tickets") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Text("👤") },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "TechField",
                color = Color(0xFF6C00FF),
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFF6C00FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(55.dp))
                }
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFF4CAF50), CircleShape)
                        .background(Color.White, CircleShape)
                        .padding(3.dp)
                        .background(Color(0xFF4CAF50), CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Datos Dinámicos del Usuario
            Text(
                text = usuarioLogueado?.nombreCompleto ?: "Cargando...",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = usuarioLogueado?.especialidad ?: "",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            SuggestionChip(
                onClick = {},
                label = {
                    Text(
                        text = if (esIT) "ADMINISTRADOR IT" else "TÉCNICO DE CAMPO",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6C00FF)
                    )
                },
                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFF0E6FF))
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "$ticketsFinalizados", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        Text("Tickets Finalizados", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF6C00FF)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "$ticketsEnCurso", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        Text("Desempeño", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PanelDeControlAjustes()

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onCerrarSesion,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ExitToApp, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (esIT) "Cerrar Sesión de IT" else "Cerrar Sesión del Técnico",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PanelDeControlAjustes() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "PANEL DE CONTROL",
                color = Color(0xFF6C00FF),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            var notificacionesActivas by remember { mutableStateOf(true) }

            SettingItemInteractive(
                icon = { Icon(Icons.Default.Notifications, null, tint = Color(0xFF6C00FF)) },
                title = "Notificaciones Push",
                action = {
                    Switch(
                        checked = notificacionesActivas,
                        onCheckedChange = { notificacionesActivas = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF6C00FF))
                    )
                }
            )

            HorizontalDivider(color = Color(0xFFF3F3F3), modifier = Modifier.padding(vertical = 4.dp))

            SettingItemInteractive(
                icon = { Icon(Icons.Default.Settings, null, tint = Color.Gray) },
                title = "Idioma predeterminado (ES)",
                action = { Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray) }
            )

            HorizontalDivider(color = Color(0xFFF3F3F3), modifier = Modifier.padding(vertical = 4.dp))

            SettingItemInteractive(
                icon = { Icon(Icons.Default.Lock, null, tint = Color.Gray) },
                title = "Seguridad y Datos locales",
                action = { Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray) }
            )
        }
    }
}

@Composable
fun SettingItemInteractive(
    icon: @Composable () -> Unit,
    title: String,
    action: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(36.dp).background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(text = title, fontWeight = FontWeight.Medium, fontSize = 15.sp, color = Color.DarkGray)
        }
        action()
    }
}