package com.example.assignment1

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.assignment1.LoginViewModel.ViewState
import com.example.assignment1.ui.theme.Purple80
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException


class TodoListViewModel(private val er: SharedPrefs?, private val api: InterfaceApi) : ViewModel(){

    private val e = er?.prefs?.getString("user_id","error")
    private val e3 = er?.prefs?.getString("id","error")
    private val _usertodos = MutableLiveData<List<TodoResponse>>()
    val usertodos : LiveData<List<TodoResponse>> = _usertodos
    private val _userdata = MutableLiveData<Todo?>(null)
    val userdata : MutableLiveData<Todo?> = _userdata
    private var _errorLiveData = MutableLiveData<String?>(null)
    val errorLiveData: MutableLiveData<String?> = _errorLiveData
    private val _viewState = MutableLiveData<com.example.assignment1.TodoListViewModel.ViewState>()
    val viewState: LiveData<com.example.assignment1.TodoListViewModel.ViewState> get() = _viewState

    fun createTodo(todo:Todo,e2: String) {

        viewModelScope.launch {
            _viewState.postValue(com.example.assignment1.TodoListViewModel.ViewState.Loading)
            try {



                    val result =
                        api.createTodos(
                            "0",
                            "Bearer $e2",
                            "48fcacf7-46e1-4285-9d47-76472c1673d1",todo
                        )
                    _viewState.postValue(com.example.assignment1.TodoListViewModel.ViewState.Success)
                    _userdata.postValue(result)

                    er?.prefs?.edit()?.apply {
                        putString("id", _userdata.value!!.id)
                        apply()
                    }








            }
            catch (ex: Exception) {
                ex.printStackTrace()
                _viewState.postValue(com.example.assignment1.TodoListViewModel.ViewState.Error("Failed"))
            }
            catch (e: HttpException) {

                // Handle HTTP errors

                _errorLiveData.value = e.response()?.errorBody()?.string()


            }

        }

    }
    private fun getTodos(e2: String){

        viewModelScope.launch {
            try {

                if (e != null) {
                    _usertodos.value = e2.let {
                        RetrofitClient.interfaceApi.getTodos(e,
                            "Bearer $e2","48fcacf7-46e1-4285-9d47-76472c1673d1")
                    }


                }




            } catch (e: HttpException) {

                // Handle HTTP errors
                _errorLiveData.value = e.response()?.errorBody()?.string()


            }

        }

    }

    private fun editTodos(todo:Todo,e2: String){

        viewModelScope.launch {
            try {

                if (e != null) {
                    _userdata.value =
                        e3?.let {
                            RetrofitClient.interfaceApi.editTodos(e,"Bearer $e2",
                                todo.id,"48fcacf7-46e1-4285-9d47-76472c1673d1",todo)
                        }


                }

            } catch (e: HttpException) {

                _errorLiveData.value = e.response()?.errorBody()?.string()





            }

        }

    }

        @Composable
        @OptIn(ExperimentalMaterial3Api::class)
        fun MainView(navController: NavController,tay: String) {




            val sheetState = rememberModalBottomSheetState()
            val scope = rememberCoroutineScope()
            var showBottomSheet by remember { mutableStateOf(false) }
            var text2 by remember { mutableStateOf("") }
            var showAlertDialog by remember {
                mutableStateOf(false)
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar =
                {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(text = stringResource(R.string.todo))
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Purple80)
                    )

                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            showBottomSheet = true
                        },
                    ) {
                        Icon(Icons.Filled.Add, "small icon")
                    }
                },
            )

            { innerPadding ->
                Column(
                    modifier = Modifier.padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,

                    )

                {
                    getTodos(tay)
                    LoopList(_usertodos,tay)

                    if (showBottomSheet) {
                        ModalBottomSheet(
                            modifier = Modifier.fillMaxHeight(),
                            sheetState = sheetState,
                            onDismissRequest = {
                                showBottomSheet = false

                            },
                        ) {
                            Column(
                                modifier = Modifier,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = text2,
                                    onValueChange = { text2 = it },
                                    placeholder = { Text(stringResource(R.string.new_todo)) },
                                    trailingIcon = {
                                        Icon(painter = painterResource(id = R.drawable.cancel_24dp_fill0_wght400_grad0_opsz24),
                                            contentDescription = "clear text",
                                            modifier = Modifier
                                                .clickable {
                                                    text2 = ""
                                                })
                                    }

                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                if (showAlertDialog) {
                                    ErrorDialog(
                                        onCancel = { showAlertDialog = false }
                                    )
                                }
                                Button(
                                    onClick = {
                                        if (text2 != "") {

                                            val t2 = Todo(text2,false,"0")
                                            createTodo(t2,tay)
                                            scope.launch {
                                                sheetState.hide()
                                            }.invokeOnCompletion {

                                                if (!sheetState.isVisible) {
                                                    showBottomSheet = false
                                                    //clear text for user
                                                    text2 = ""
                                                }
                                            }
                                        } else {
                                            showAlertDialog = true
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()


                                ) {
                                    Text(stringResource(R.string.save))

                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                OutlinedButton(
                                    onClick = {
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                showBottomSheet = false
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(stringResource(R.string.cancel))
                                }
                                OutlinedButton(
                                    onClick = { navController.navigate("ScreenOne") },
                                    modifier = Modifier.fillMaxWidth()
                                ) {

                                    Text("Log out")
                                }
                            }

                        }
                    }
                }
                val errorMessage3 by errorLiveData.observeAsState()
                errorMessage3?.let { message ->ErrorDialog2(message = message, onDismiss = { clearError()})}


            }
        }


    @Composable
    fun LoopList(viewModel: MutableLiveData<List<TodoResponse>>, tay : String) {
        val todos by viewModel.observeAsState()
        var bool: Boolean
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .border(width = 12.dp, color = Color.White)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
        ) {
            LazyColumn(modifier = Modifier) {
                todos?.let { todoResponses ->
                    items(todoResponses.size) { description ->
                        todos?.get(description)?.let {
                            bool = if(it.completed == 1) {
                                true
                            }else{
                                false
                            }
                            PrintList(it.description,bool,tay,it.id) }
                    }
                }
            }
        }
    }



    @Composable
    fun PrintList(item: String, complete: Boolean, tay:String, id:String) {
        var checked by remember { mutableStateOf(complete) }

        //stack on rows
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .background(Purple80),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically

        ){

            Text(text = item,
                color = Color.Black,modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(1f))

            Checkbox(

                checked = checked,
                onCheckedChange = {checked = it; editTodos(Todo(item,checked,id),tay) },

                modifier = Modifier.padding(vertical = 12.dp)

            )

        }
    }
    @Composable
    fun ErrorDialog2(message: String, onDismiss: () -> Unit) {
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
    sealed class ViewState {
        data object Loading : ViewState()
        data class Error(val message: String) : ViewState()
        data object Success : ViewState()
    }
    private fun clearError() {
        _errorLiveData.value = null
    }
}