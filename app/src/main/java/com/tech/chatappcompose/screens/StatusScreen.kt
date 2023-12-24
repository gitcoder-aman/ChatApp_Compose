package com.tech.chatappcompose.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tech.chatappcompose.bottomNav.DestinationScreen
import com.tech.chatappcompose.ui.theme.SkyColor
import com.tech.chatappcompose.utils.CommonDivider
import com.tech.chatappcompose.utils.CommonProgressBar
import com.tech.chatappcompose.utils.CommonRow
import com.tech.chatappcompose.utils.TitleText
import com.tech.chatappcompose.utils.navigateTo
import com.tech.chatappcompose.viewmodel.LCViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusScreen(navController: NavHostController, vm: LCViewModel) {

    val context = LocalContext.current
    val inProgress = vm.inProgressStatus.value
    if (inProgress) {
        CommonProgressBar()
    } else {
        val statuses = vm.status.value
        val userData = vm.userData.value

        val myStatuses = statuses.filter {
            it.user.userId == userData?.userId
        }
        val otherStatuses = statuses.filter {
            it.user.userId != userData?.userId
        }

        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()){uri->
            uri.let {
                vm.uploadStatus(uri,context)
            }
        }
        Scaffold(
            floatingActionButton = {
                FAB {
                    launcher.launch("image/*")
                }
            }, containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                TitleText(text = "Status")
                if (statuses.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "No Statuses Available")
                    }
                } else {
                    if (myStatuses.isNotEmpty()) {
                        CommonRow(
                            imageUrl = myStatuses[0].user.imageUrl!!,
                            name = myStatuses[0].user.name!!
                        ) {
                            navigateTo(
                                navController,
                                DestinationScreen.SingleStatus.createRoutes(myStatuses[0].user.userId!!)
                            )
                        }
                        CommonDivider()
                        //unique user get
                        val uniqueUsers = otherStatuses.map {
                            it.user
                        }.toSet().toList()

                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(uniqueUsers) { user ->
                                CommonRow(imageUrl = user.imageUrl!!, name = user.name!!) {
                                    navigateTo(
                                        navController,
                                        DestinationScreen.SingleStatus.createRoutes(user.userId!!)
                                    )
                                }
                            }
                        }
                    }
                }
                BottomNavigationMenu(
                    selectedItem = BottomNavigationItem.STATUSLIST,
                    navController = navController
                )
            }
        }

    }

}

@Composable
fun FAB(
    onFabClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onFabClick,
        containerColor = SkyColor,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 40.dp)
    ) {

        Icon(
            imageVector = Icons.Rounded.Edit,
            contentDescription = "Add Status",
            tint = Color.White
        )
    }
}