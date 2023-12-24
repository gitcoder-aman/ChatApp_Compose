package com.tech.chatappcompose.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tech.chatappcompose.data.Message
import com.tech.chatappcompose.ui.theme.PurpleGrey80
import com.tech.chatappcompose.ui.theme.SkyColor
import com.tech.chatappcompose.utils.CommonImage
import com.tech.chatappcompose.viewmodel.LCViewModel

@Composable
fun SingleChatScreen(navController: NavHostController, vm: LCViewModel, chatId: String) {

    var reply by rememberSaveable {
        mutableStateOf("")
    }
    val onSendReply = {
        vm.onSendReply(chatId, reply)
        reply = ""
    }
    val myUser = vm.userData.value
    val currentChat = vm.chats.value.first { it.chatId == chatId }
    val chatUser =
        if (myUser?.userId == currentChat.user1.userId) currentChat.user2 else currentChat.user1
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        vm.populateMessage(chatId, context)
    }
    BackHandler {
        vm.depopulateMessage()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Header(
            chatUserName = chatUser.name ?: "",
            chatUserImage = chatUser.imageUrl ?: ""
        ) {
            navController.popBackStack()
            vm.depopulateMessage()
        }
        MessageBox(
            modifier = Modifier.weight(1f),
            chatMessages = vm.chatMessages.value,
            currentUserId = myUser?.userId ?: ""
        )
        ReplyBox(reply = reply, onReplyChange = { reply = it }, onSendReply = onSendReply)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplyBox(
    reply: String, onReplyChange: (String) -> Unit, onSendReply: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {

        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = reply, onValueChange = onReplyChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = SkyColor,
                    selectionColors = TextSelectionColors(
                        handleColor = SkyColor,
                        backgroundColor = SkyColor
                    )
                )
            )

            Button(
                onClick = onSendReply,
                modifier = Modifier.size(width = 80.dp, height = 40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SkyColor,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Send", style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400
                    )
                )

            }
        }
    }
}

@Composable
fun Header(
    chatUserName: String,
    chatUserImage: String,
    onBackClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp)
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier
                    .size(35.dp)
                    .clickable {
                        onBackClick.invoke()
                    })
            Spacer(modifier = Modifier.padding(start = 12.dp))

            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(50.dp)
            ) {
                CommonImage(
                    data = chatUserImage,
                    modifier = Modifier
                        .padding(2.dp)
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(SkyColor)
                )
            }

            Text(
                text = chatUserName, style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W400,
                    color = Color.Black
                )
            )
        }
    }
}

@Composable
fun MessageBox(
    modifier: Modifier,
    chatMessages: List<Message>,
    currentUserId: String
) {
    LazyColumn(modifier = modifier) {
        items(chatMessages) { msg ->

            val alignment = if (msg.sendBy == currentUserId) Alignment.End else Alignment.Start
            val color = if (msg.sendBy == currentUserId) Color(0xFF68C400) else Color(0xFFC0C0C0)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), horizontalAlignment = alignment
            ) {
                Text(
                    text = msg.message ?: "",
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color)
                        .padding(12.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}