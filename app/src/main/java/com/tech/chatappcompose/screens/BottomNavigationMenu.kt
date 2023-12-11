package com.tech.chatappcompose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tech.chatappcompose.R
import com.tech.chatappcompose.bottomNav.DestinationScreen
import com.tech.chatappcompose.utils.navigateTo

enum class BottomNavigationItem(val icon: Int, val navDestination: DestinationScreen) {

    CHATLIST(R.drawable.chat_icon, DestinationScreen.ChatList),
    STATUSLIST(R.drawable.status_icon, DestinationScreen.StatusList),
    PROFILE(R.drawable.profile_icon, DestinationScreen.Profile)
}

@Composable
fun BottomNavigationMenu(
    selectedItem: BottomNavigationItem,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 4.dp)
            .background(Color.White)
    ) {
        for (item in BottomNavigationItem.entries) {
            Image(
                painter = painterResource(id = item.icon),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp)
                    .weight(1f)
                    .clickable {
                        navigateTo(navController, item.navDestination.routes)
                    },
                colorFilter = if (item == selectedItem) ColorFilter.tint(color = Color.Black) else ColorFilter.tint(
                    Color.Gray
                )
            )
        }
    }
}