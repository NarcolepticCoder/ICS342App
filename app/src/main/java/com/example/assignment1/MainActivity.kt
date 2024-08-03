package com.example.assignment1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedPref = SharedPrefs(this)


            val vm2 = RegisterViewModel(sharedPref)
            val vm = LoginViewModel(sharedPref)

            val vm3 = TodoListViewModel(sharedPref)

            val navController = rememberNavController()
            val errorMessage2 by vm2.errorLiveData.observeAsState()
            val errorMessage by vm.errorLiveData.observeAsState()
            val er by vm.userdata.observeAsState()

            //Error message for register
            errorMessage?.let { message ->
                vm.ErrorDialog(message = message,onDismiss = { vm.clearError()})

            }
            //Error message for login
            errorMessage2?.let { message ->
                vm2.ErrorDialog(message = message,onDismiss = { vm2.clearError()})
            }


            NavHost(navController = navController, "ScreenOne"){
                composable("ScreenOne"){
                    vm.Start(onButtonClicked = {
                        //register screen
                        navController.navigate("ScreenTwo")
                    },navController)

                }
                composable("ScreenTwo"){
                    vm2.Start2(onButtonClicked = {
                        //login screen
                        navController.navigate("ScreenOne")
                    },navController
                    )
                }
                composable("ScreenThree"){
                    er?.let { it1 -> vm3.MainView(navController, it1.token) }
                }

            }




        }
    }
}






@Composable
fun ErrorDialog(
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {  },
        confirmButton = {
            TextButton(onClick = onCancel) {
                Text(text = "Okay")
            }
        },

        title = {
            Text(text = "Error!")
        },
        text = {
            Text(text = "You cannot create a new empty todo!")
        }
    )
}


