package com.example.saturdaytest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.saturdaytest.ui.theme.SaturdayTestTheme
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaturdayTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MyApp()
                }
            }
        }
    }
}

@Composable
fun MyApp( testViewModel: TestViewModel = TestViewModel()) {

    val navController = rememberNavController()

    var appTitle by remember { mutableStateOf ("")}
    val setAppTitle : (String) -> Unit = { string -> appTitle = string }

    Scaffold(
        topBar = {

            if(appTitle == "Contacts") {
                TopAppBar(
                    title = { Text(text = appTitle) },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(imageVector = Icons.Filled.Search, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                        }
                    }
                )
            }else{
                TopAppBar(
                    title = { Text(text = appTitle) },
                    navigationIcon = {
                        Text(
                            text = "Cancel",
                            modifier = Modifier.clickable { navController.navigate("HomeScreen") }
                        )
                    },
                    actions = {
                        Text (
                            text = "Save",
                            modifier = Modifier.clickable {  }
                        )
                    }
                )

            }
        },

    ) {

        ContactNavHost(navController = navController , testViewModel ,setAppTitle  )
    }

}

@Composable
fun contactList(
    contactList : List<Contact> ,
    navigateToItemDetail : (String) -> Unit,
    setAppTitle : (String) -> Unit,
){

    LaunchedEffect(Unit){
        setAppTitle("Contacts")
    }

    Column( modifier = Modifier.fillMaxSize() ){

        LazyColumn( modifier = Modifier, ){

            items(
                contactList,
                key = { item -> item.id}
            ){
                    item -> contactItem(item = item, { navigateToItemDetail(item.id) })
            }
        }
    }

}

@Composable
fun contactItem( item : Contact , navigateToItemDetail : () -> Unit) {

    Row(modifier = Modifier
        .padding(8.dp)
        .clickable { navigateToItemDetail() }
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Image(
            painter = painterResource(id = R.drawable.download),
            contentDescription = null
        )
        Text(
            text = "${item.firstName} ${item.lastName}",
            color = Color.Black,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        )


    }
}

@Composable
fun DetailsScreen (
    contact: Contact,
    onClickToUpdate : (String,String,String,String) -> Unit,
    isItemValid : (String,String) -> Boolean,
    navigateToHomeScreen : () -> Unit,
    setAppTitle : (String) -> Unit,
){

    LaunchedEffect(Unit){
        setAppTitle("")
    }

    var firstName by remember { mutableStateOf(contact.firstName)}
    var lastName by remember { mutableStateOf(contact.lastName)}
    var email by remember { mutableStateOf(contact.email)}
    var phone by remember { mutableStateOf(contact.phone)}

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ){

        Image(
            painter = painterResource(id = R.drawable.download),
            contentDescription = null
        )

        Row(){
            Text(
                text = "Main Information",
                modifier = Modifier
                    .weight(1f)
                    .background(Color.LightGray)
            )
        }

        Row( verticalAlignment = Alignment.CenterVertically ) {
            Text(
                text ="First Name",
                modifier = Modifier.weight(0.3f)
            )
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                modifier = Modifier.weight(1f)
                )
        }

        Row( verticalAlignment = Alignment.CenterVertically ) {
            Text(
                text ="Last Name",
                modifier = Modifier.weight(0.3f)
            )
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                modifier = Modifier.weight(1f)
            )
        }

        Row(){
            Text(
                text = "Sub Information",
                modifier = Modifier
                    .weight(1f)
                    .background(Color.LightGray)
            )
        }

        Row( verticalAlignment = Alignment.CenterVertically) {
            Text(
                text ="Email Name",
                modifier = Modifier.weight(0.3f)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.weight(1f)
            )
        }

        Row( verticalAlignment = Alignment.CenterVertically ) {
            Text(
                text ="Phone Name",
                modifier = Modifier.weight(0.3f)
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = {

                if(isItemValid(firstName,lastName)) {
                    onClickToUpdate(firstName, lastName, email, phone)
                    navigateToHomeScreen()
                }

            }) {

            Text(
                text = "Update"
            )
        }
    }
}

@Composable
fun ContactNavHost(
    navController: NavHostController,
    testViewModel: TestViewModel,
    setAppTitle : (String) -> Unit,
){

    val contactList : List<Contact> = testViewModel.contactList

    NavHost(
        navController = navController,
        startDestination = "HomeScreen",
    ){

        composable("HomeScreen"){
            contactList(
                contactList = contactList ,
                navigateToItemDetail = { id -> navigateToSingleContact(navController,id) },
                setAppTitle = { title -> setAppTitle(title)},
            )
        }

        composable(
            route = "detail/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            ),

            ) { entry ->
            val contactId = entry.arguments?.getString("id")
            val contact = contactId?.let { testViewModel.getContact(it) }

            contact!!.let { it ->
                DetailsScreen(
                    contact = it,
                    onClickToUpdate = { firstName,lastName,email,phone -> testViewModel.update(it.id,firstName,lastName,email,phone) },
                    isItemValid = { firstName, lastName -> testViewModel.isContactValid(firstName,lastName)},
                    navigateToHomeScreen = { navController.navigate("HomeScreen")},
                    setAppTitle = { title -> setAppTitle(title)},
                )
            }

        }

    }
}

private fun navigateToSingleContact(navController: NavHostController, id: String) {
    navController.navigate("detail/$id")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SaturdayTestTheme {

    }
}