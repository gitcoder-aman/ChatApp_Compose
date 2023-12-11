package com.tech.chatappcompose.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.tech.chatappcompose.viewmodel.LCViewModel

@Composable
fun StatusScreen(navController: NavHostController, vm: LCViewModel) {
    BottomNavigationMenu(
        selectedItem = BottomNavigationItem.STATUSLIST,
        navController = navController
    )
}