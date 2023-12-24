package com.tech.chatappcompose.bottomNav

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.tech.chatappcompose.screens.LoginScreen
import androidx.navigation.compose.composable
import com.tech.chatappcompose.screens.ChatListScreen
import com.tech.chatappcompose.screens.ProfileScreen
import com.tech.chatappcompose.screens.SignupScreen
import com.tech.chatappcompose.screens.SingleChatScreen
import com.tech.chatappcompose.screens.SingleStatusScreen
import com.tech.chatappcompose.screens.StatusScreen
import com.tech.chatappcompose.viewmodel.LCViewModel

@Composable
fun ChatAppNavigation() {

    val navController = rememberNavController()
    var vm = hiltViewModel<LCViewModel>()

    NavHost(navController = navController, startDestination = DestinationScreen.SignUp.routes) {
        composable(DestinationScreen.SignUp.routes) {
            SignupScreen(navController, vm)
        }
        composable(DestinationScreen.Login.routes) {
            LoginScreen(navController, vm)
        }
        composable(DestinationScreen.ChatList.routes) {
            ChatListScreen(navController, vm)
        }
        composable(DestinationScreen.SingleChat.routes) {
            val chatId = it.arguments?.getString("chatId")
            chatId?.let {
                SingleChatScreen(navController, vm, chatId)
            }
        }
        composable(DestinationScreen.StatusList.routes) {
            StatusScreen(navController, vm)
        }
        composable(DestinationScreen.Profile.routes) {
            ProfileScreen(navController, vm)
        }
        composable(DestinationScreen.SingleStatus.routes) {
            val userId = it.arguments?.getString("userId")
            userId?.let {
                SingleStatusScreen(navController, vm, userId)
            }
        }
    }

}