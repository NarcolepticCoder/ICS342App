package com.example.assignment1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assignment1.ui.theme.Assignment1Theme
import com.example.assignment1.ui.theme.Purple80
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainView()

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView() {
    val notesList = remember {
            mutableStateListOf<String>()
    }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var text2 by remember { mutableStateOf("") }
    var showAlertDialog by remember {
        mutableStateOf(false)
    }

    Assignment1Theme {
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
                LoopList(notesList)

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
                                placeholder = {Text(stringResource(R.string.new_todo))},
                                trailingIcon = {
                                    Icon(painter = painterResource(id = R.drawable.cancel_24dp_fill0_wght400_grad0_opsz24), contentDescription = "clear text",
                                        modifier = Modifier
                                            .clickable {
                                                text2 = ""
                                            })
                                }

                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            if(showAlertDialog) {
                                ErrorDialog(
                                    onCancel = { showAlertDialog = false }
                                )
                            }
                                Button(
                                    onClick = {
                                    if(text2 != "") {
                                        notesList.add(text2)
                                        scope.launch { sheetState.hide()
                                        }.invokeOnCompletion {

                                            if (!sheetState.isVisible) {
                                                showBottomSheet = false
                                                //clear text for user
                                                text2 = ""
                                            }
                                        }
                                    }else{
                                        showAlertDialog = true
                                    }
                                  },
                                    modifier = Modifier.fillMaxWidth()


                                ) {
                                    Text(stringResource(R.string.save))

                                }
                                Spacer(modifier = Modifier.height(20.dp))

                            OutlinedButton(onClick = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showBottomSheet = false
                                    }
                                }
                            },
                                modifier = Modifier.fillMaxWidth()) {
                                Text(stringResource(R.string.cancel))
                            }
                       }
                   }



                }


            }
        }
    }
}



@Composable
fun LoopList(itemList: SnapshotStateList<String>) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .border(width = 12.dp, color = Color.White)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
    ) {
    LazyColumn(modifier = Modifier) {
        items(itemList.size) { index ->
            PrintList(item = itemList[index])
        }
    }
    }

}
@Composable
fun PrintList(item: String) {
    var checked by remember { mutableStateOf(true) }

    //stack on rows
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(Purple80),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically



        ){

        Text(text = item,
            color = Color.Black,modifier = Modifier.padding(horizontal = 12.dp).weight(1f))


        Checkbox(

            checked = checked,
            onCheckedChange = { checked = it },
            modifier = Modifier.padding(vertical = 12.dp)

        )

    }
}
@Composable
fun ErrorDialog(
    onCancel: () -> Unit,
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


@Preview(showBackground = true)
@Composable
fun MainPreview() {
    Assignment1Theme {
        MainView()
    }
}