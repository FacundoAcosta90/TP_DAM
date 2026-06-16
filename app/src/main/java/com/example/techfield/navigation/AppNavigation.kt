package com.techfield.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.techfield.ui.screens.AddEditTicketScreen
import com.techfield.ui.screens.CameraScreen
import com.techfield.ui.screens.ProfileScreen
import com.techfield.ui.screens.TicketsScreen
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
            TicketsScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("addTicket") {
            AddEditTicketScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("camera") {
            CameraScreen()
        }

        composable("profile") {
            ProfileScreen()
        }
    }
}