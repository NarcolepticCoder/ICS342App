package com.example.assignment1

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import retrofit2.HttpException


class LoginViewModel(er: SharedPrefs) : ViewModel() {
    private val _userdata = MutableLiveData<NewUser?>(null)
    val userdata : MutableLiveData<NewUser?> = _userdata
    private val _errorLiveData = MutableLiveData<String?>()
    val errorLiveData: MutableLiveData<String?> = _errorLiveData
    private val editor: SharedPreferences.Editor = er.prefs.edit()

    private fun loginUser(user:UserInfoRequest, navController: NavController){

        viewModelScope.launch {
            try {

                _userdata.value = RetrofitClient.interfaceApi.loginUser("48fcacf7-46e1-4285-9d47-76472c1673d1",user)
                editor.apply {

                    putString("user_id", _userdata.value!!.user_id)
                    putString("token", _userdata.value!!.token)
                    apply()
                }

                navController.navigate("ScreenThree")



            } catch (e: HttpException) {

                // Handle HTTP errors
                _errorLiveData.value = e.response()?.errorBody()?.string()




            }

        }

    }


    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    fun Start(onButtonClicked: () -> Unit,navController: NavController) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val newuser = UserInfoRequest(null,email, password)
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            Text("Todo",modifier = Modifier.align(Alignment.TopCenter).offset(x = 0.dp, y = 200.dp), fontSize = 40.sp)
        }

        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(value = email,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {email = it},
                label = { Text("Email address") })
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(value = password,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {password = it},
                label = { Text("Password") })
            Spacer(modifier = Modifier.height(20.dp))
            Button(modifier = Modifier.fillMaxWidth(), onClick = { loginUser(newuser,navController) }) {

                Text("Log in")

            }

            Spacer(modifier = Modifier.height(40.dp))
            TextButton(
                onClick = onButtonClicked
            ) {
                Text("Create an account")
            }
        }
    }
    @Composable
    fun ErrorDialog(message: String, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Error") },
            text = { Text(message) },
            confirmButton = {
                Button(onClick = { onDismiss() }) {
                    Text("OK")
                }
            }
        )

    }
    fun clearError() {
        _errorLiveData.value = null
    }
}




