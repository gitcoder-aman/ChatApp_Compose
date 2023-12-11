package com.tech.chatappcompose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tech.chatappcompose.R
import com.tech.chatappcompose.bottomNav.DestinationScreen
import com.tech.chatappcompose.ui.theme.LightGreen
import com.tech.chatappcompose.ui.theme.SkyColor
import com.tech.chatappcompose.utils.CheckSignedIn
import com.tech.chatappcompose.utils.CommonProgressBar
import com.tech.chatappcompose.utils.navigateTo
import com.tech.chatappcompose.viewmodel.LCViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(navController: NavHostController, vm: LCViewModel) {

    val context = LocalContext.current
    CheckSignedIn(vm = vm, navController = navController)
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val nameState = remember {
                mutableStateOf(TextFieldValue())
            }
            val numberState = remember {
                mutableStateOf(TextFieldValue())
            }
            val emailState = remember {
                mutableStateOf(TextFieldValue())
            }
            val passwordState = remember {
                mutableStateOf(TextFieldValue())
            }
            val focus = LocalFocusManager.current
            Image(
                painter = painterResource(id = R.drawable.chat_image),
                contentDescription = null,
                modifier = Modifier
                    .width(200.dp)
                    .padding(top = 16.dp)
                    .padding(8.dp)
            )
            Text(
                text = "Sign Up", fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )

            OutlinedTextField(value = nameState.value, onValueChange = {
                nameState.value = it
            },
                label = { Text(text = "Name") },
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(value = numberState.value, onValueChange = {
                numberState.value = it
            },
                label = { Text(text = "Number") },
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(value = emailState.value, onValueChange = {
                emailState.value = it
            },
                label = { Text(text = "Email") },
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(value = passwordState.value, onValueChange = {
                passwordState.value = it
            },
                label = { Text(text = "Password") },
                modifier = Modifier.padding(8.dp)
            )
            Button(onClick = {
                             vm.signUp(
                                 nameState.value.text,
                                 numberState.value.text,
                                 emailState.value.text,
                                 passwordState.value.text,
                                 context = context
                             )
            },
                modifier = Modifier.padding(8.dp), colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = SkyColor
                )) {
                Text(text = "SIGN UP")
            }
            Text(text = "Already a User? Go to login- >",
                color = Color.Blue,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navigateTo(navController, DestinationScreen.Login.routes)
                    })
        }
    }
    if(vm.inProgress.value){
        CommonProgressBar()
    }
}