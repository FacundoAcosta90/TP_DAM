package com.techfield.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techfield.viewmodel.TicketViewModel

@Composable
fun ProfileScreen(
    viewModel: TicketViewModel,
    onLogout: () -> Unit
) {
    // Leemos el usuario logueado actualmente en el sistema
    val usuario = viewModel.usuarioLogueado.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Mi Perfil",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Muestra el nombre real (Juan o Marcos)
        Text(
            text = "Usuario: ${usuario?.nombreCompleto ?: "No identificado"}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Muestra la especialidad/rol (IT o Técnico)
        Text(
            text = "Área: ${usuario?.especialidad ?: "Sin especificar"}",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Botón de cierre de sesión dinámico
        Button(
            onClick = {
                viewModel.setUsuarioLogueado(null) // Limpiamos la sesión en el ViewModel
                onLogout() // Volvemos al Login
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(
                text = "Cerrar Sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}