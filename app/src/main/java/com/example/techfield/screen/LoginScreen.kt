package com.techfield.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techfield.R
import com.techfield.viewmodel.TicketViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: TicketViewModel,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }


    var errorCredenciales by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFF6C00FF)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_tools),
                contentDescription = "Logo TechField",
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("TechField", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Accede a tu panel de control", color = Color.Gray, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(32.dp))


        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorCredenciales = false
            },
            label = { Text("Usuario") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            isError = errorCredenciales,
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorCredenciales = false
            },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(Icons.Default.Info, contentDescription = null)
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = errorCredenciales,
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth()
        )


        if (errorCredenciales) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Usuario o contraseña incorrectos",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(32.dp))


        Button(
            onClick = {
                Log.d("LOGIN", "CLICK BOTON")


                coroutineScope.launch {
                    val usuarioDb = viewModel.autenticarUsuario(email)

                    if (usuarioDb != null && usuarioDb.contrasenia == password) {
                        Log.d("LOGIN", "LOGIN OK VIA ROOM")
                        onLoginSuccess()
                    } else {
                        Log.d("LOGIN", "LOGIN FAIL VIA ROOM")
                        errorCredenciales = true
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C00FF)),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Iniciar Sesión",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}