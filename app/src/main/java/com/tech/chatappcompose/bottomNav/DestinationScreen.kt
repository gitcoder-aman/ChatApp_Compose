package com.tech.chatappcompose.bottomNav

sealed class DestinationScreen(var routes: String){
    object SignUp : DestinationScreen("signup")
    object Login : DestinationScreen("login")
    object Profile : DestinationScreen("profile")
    object ChatList : DestinationScreen("chatList")
    object SingleChat : DestinationScreen("singleChat/{chatId}"){
        fun createRoutes(chatId : String) = "singlechat/$chatId"
    }
    object StatusList : DestinationScreen("statusList")
    object SingleStatus : DestinationScreen("singleStatus/{userId}"){
        fun createRoutes(userId : String) = "singleStatus/$userId"
    }
}
