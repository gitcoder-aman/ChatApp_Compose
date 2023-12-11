package com.tech.chatappcompose.screens

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tech.chatappcompose.bottomNav.DestinationScreen
import com.tech.chatappcompose.ui.theme.SkyColor
import com.tech.chatappcompose.utils.CommonDivider
import com.tech.chatappcompose.utils.CommonImage
import com.tech.chatappcompose.utils.CommonProgressBar
import com.tech.chatappcompose.utils.navigateTo
import com.tech.chatappcompose.viewmodel.LCViewModel

@Composable
fun ProfileScreen(navController: NavHostController, vm: LCViewModel) {
    val inProgress = vm.inProgress.value
    if (inProgress) {
        CommonProgressBar()
    } else {
        val context = LocalContext.current
        val userData = vm.userData.value
        var name by rememberSaveable {
            mutableStateOf(userData?.name ?: "")
        }
        var number by rememberSaveable {
            mutableStateOf(userData?.number ?: "")
        }
        Column(
            modifier = Modifier.background(Color.White),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ProfileContent(
                context = context,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                vm = vm,
                name = name,
                number = number,
                onNameChange = { name = it },
                onNumberChange = { number = it },
                onBack = {
                    navigateTo(navController, DestinationScreen.ChatList.routes)
                },
                onSave = {
                         vm.createOrUpdateProfile(
                             name = name,
                             number = number,
                             context = context
                         )
                },
                onLogout = {
                    vm.logout(context)
                    navController.navigate(DestinationScreen.Login.routes) {
                        popUpTo(0)
                    }
                }
            )
            BottomNavigationMenu(
                selectedItem = BottomNavigationItem.PROFILE,
                navController = navController
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    modifier: Modifier,
    context: Context,
    vm: LCViewModel,
    name: String,
    number: String,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onLogout: () -> Unit
) {

    val imageUrl = vm.userData.value?.imageUrl

    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Back", modifier = Modifier.clickable {
                onBack.invoke()
            })
            Text(text = "Save", modifier = Modifier.clickable {
                onSave.invoke()
            })
        }
        CommonDivider()
        ProfileImage(imageUrl = imageUrl, vm, context = context)
        CommonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Name", modifier = Modifier.width(100.dp))
            TextField(
                value = name,
                onValueChange = onNameChange,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    textColor = Color.Black
                )
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Mob No.", modifier = Modifier.width(100.dp))
            TextField(
                value = number,
                onValueChange = onNumberChange,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    textColor = Color.Black
                )
            )
        }
        CommonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                onLogout.invoke()
            }, colors = ButtonDefaults.buttonColors(
                containerColor = SkyColor,
                contentColor = Color.White
            )) {
                Text(text = "Logout")
            }
        }
    }
}

@Composable
fun ProfileImage(imageUrl: String?, vm: LCViewModel,context: Context) {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { //when uri is not null then go
            vm.uploadProfileImage(uri,context)
        }
    }
    Box(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min)) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    launcher.launch("image/*")
                }, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
            ) {
                CommonImage(data = imageUrl)
            }
            Text(text = "Change Profile Pic")
        }
        if (vm.inProgress.value) {
            CommonProgressBar()
        }
    }
}