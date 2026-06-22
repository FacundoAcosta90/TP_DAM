package com.techfield.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.techfield.ui.screens.AddEditTicketScreen
import com.techfield.ui.screens.CameraScreen
import com.techfield.ui.screens.ProfileScreen
import com.techfield.ui.screens.TicketsScreen
import com.techfield.ui.screens.TicketDetailScreen
import com.techfield.viewmodel.TicketViewModel

@Composable
fun AppNavigation(
    viewModel: TicketViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "tickets"
    ) {
        composable("tickets") {
            TicketsScreen(navController = navController, viewModel = viewModel)
        }

        composable("addTicket") {
            AddEditTicketScreen(navController = navController, viewModel = viewModel)
        }

        composable("camera") {
            CameraScreen()
        }

        composable("profile") {
            ProfileScreen(viewModel = viewModel, onLogout = {})
        }

        composable(
            route = "ticketDetail/{ticketId}",
            arguments = listOf(navArgument("ticketId") { type = NavType.IntType })
        ) { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getInt("ticketId") ?: 0
            val tickets by viewModel.tickets.collectAsState(initial = emptyList())
            val ticketSeleccionado = tickets.find { it.id == ticketId }

            ticketSeleccionado?.let { ticket ->
                TicketDetailScreen(
                    ticket = ticket,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}