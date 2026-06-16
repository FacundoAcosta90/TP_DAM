package com.example.techfield.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class OnboardingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val sharedPref = getSharedPreferences("TechFieldPrefs", Context.MODE_PRIVATE)
        val onboardingCompletado = sharedPref.getBoolean("onboarding_done", false)

        if (onboardingCompletado) {
            irAlLogin()
            return
        }

        setContent {
            MaterialTheme {
                OnboardingScreen(
                    onFinalizado = {
                        sharedPref.edit().putBoolean("onboarding_done", true).apply()
                        irAlLogin()
                    }
                )
            }
        }
    }

    private fun irAlLogin() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinalizado: () -> Unit) {
    val paginas = listOf(
        OnboardingData("🎫", "Gestión de Tickets", "Recibí y administrá tus órdenes de trabajo técnico en tiempo real, directo desde la planta o el cliente."),
        OnboardingData("📸", "Evidencia Fotográfica", "Capturá fotos con la cámara del dispositivo para dejar constancia del estado del equipamiento antes y después."),
        OnboardingData("📝", "Bitácora de Campo", "Escribí comentarios y notas detalladas en cada ticket para mantener un historial crítico de mantenimiento.")
    )

    val pagerState = rememberPagerState(pageCount = { paginas.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color(0xFFF7F7F7)
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            OnboardingIndicators(paginasSize = paginas.size, currentPage = pagerState.currentPage)


            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(datos = paginas[page])
            }


            OnboardingBottomButtons(
                pagerState = pagerState,
                paginasSize = paginas.size,
                coroutineScope = coroutineScope,
                onFinalizado = onFinalizado
            )
        }
    }
}


@Composable
fun OnboardingPageContent(datos: OnboardingData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(text = datos.emoji, fontSize = 90.sp, modifier = Modifier.padding(bottom = 24.dp))
        Text(
            text = datos.titulo,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6C00FF),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = datos.descripcion,
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}


@Composable
fun OnboardingIndicators(paginasSize: Int, currentPage: Int) {
    Row(
        Modifier.wrapContentHeight().fillMaxWidth().padding(top = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(paginasSize) { iteration ->
            val color = if (currentPage == iteration) Color(0xFF6C00FF) else Color(0xFFE0E0E0)
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(10.dp)
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingBottomButtons(
    pagerState: PagerState,
    paginasSize: Int,
    coroutineScope: CoroutineScope,
    onFinalizado: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (pagerState.currentPage < paginasSize - 1) {
            TextButton(onClick = onFinalizado) {
                Text("Omitir", color = Color.Gray, fontWeight = FontWeight.SemiBold)
            }
        } else {
            Spacer(modifier = Modifier.width(60.dp))
        }

        Button(
            onClick = {
                if (pagerState.currentPage < paginasSize - 1) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    onFinalizado()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C00FF)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(48.dp)
        ) {
            Text(
                text = if (pagerState.currentPage == paginasSize - 1) "Comenzar" else "Siguiente",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

data class OnboardingData(
    val emoji: String,
    val titulo: String,
    val descripcion: String
)