package com.tech.chatappcompose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
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
fun ChatListScreen(navController: NavHostController, vm: LCViewModel) {

    val inProgressChat = vm.inProgressChat
    if (inProgressChat.value) {
        CommonProgressBar()
    } else {
        val context = LocalContext.current
        val chats = vm.chats.value
        val userData = vm.userData.value
        val showDialog = remember {
            mutableStateOf(false)
        }
        val onFabClick: () -> Unit = { showDialog.value = true }
        val onDismiss: () -> Unit = { showDialog.value = false }
        val onAddChat: (String) -> Unit = {
            vm.onAddChat(it, context)
            showDialog.value = false
        }
        Scaffold(
            containerColor = Color.White,
            floatingActionButton = {
                FAB(
                    showDialog = showDialog.value,
                    onFabClick = onFabClick,
                    onDismiss = onDismiss,
                    onAddChat = onAddChat
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    TitleText(text = "Chats")
                    CommonDivider()
                    if (chats.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "No Chats Available.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f)
                        ) {
                            items(chats) { chat ->
                                val chatUser = if (chat.user1.userId == userData?.userId) {
                                    chat.user2
                                } else {
                                    chat.user1
                                }
                                CommonRow(imageUrl = chatUser.imageUrl!!, name = chatUser.name!!) {
                                    chat.chatId?.let {
                                        navigateTo(
                                            navController = navController,
                                            route = DestinationScreen.SingleChat.createRoutes(chatId = it)
                                        )
                                    }
                                }
                                CommonDivider()
                            }
                        }
                    }
                    BottomNavigationMenu(
                        selectedItem = BottomNavigationItem.CHATLIST,
                        navController = navController
                    )
                }
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAB(
    showDialog: Boolean,
    onFabClick: () -> Unit,
    onDismiss: () -> Unit,
    onAddChat: (String) -> Unit
) {
    val addChatNumber = remember {
        mutableStateOf("")
    }
    if (showDialog) {
        AlertDialog(onDismissRequest = {
            onDismiss.invoke()
            addChatNumber.value = ""
        },
            confirmButton = {
                Button(
                    onClick = {
                        onAddChat(addChatNumber.value)
                    }, colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = SkyColor
                    )
                ) {
                    Text(text = "Add Chat")
                }
            }, title = { Text(text = "Add Chat") },
            text = {
                OutlinedTextField(
                    value = addChatNumber.value, onValueChange = {
                        addChatNumber.value = it
                    }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Color.Black,
                        selectionColors = TextSelectionColors(
                            handleColor = SkyColor,
                            backgroundColor = SkyColor
                        ), cursorColor = SkyColor,
                        focusedIndicatorColor = SkyColor
                    )
                )
            }
        )

    }
    FloatingActionButton(
        contentColor = Color.White,
        onClick = {
            onFabClick.invoke()
        }, containerColor = SkyColor,
        shape = CircleShape, modifier = Modifier.padding(bottom = 40.dp)
    ) {
        Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = Color.White)
    }
}