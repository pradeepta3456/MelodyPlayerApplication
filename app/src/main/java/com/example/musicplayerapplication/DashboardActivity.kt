package com.example.musicplayerapplication
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.musicplayerapplication.repository.HomeRepo
import com.example.musicplayerapplication.repository.HomeRepoImpl
import com.example.musicplayerapplication.viewmodel.HomeViewModel


class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody() {
    val context = LocalContext.current
    val activity = context as Activity
    data class NavItem(val label:String,val icon: Int)

    var selectedindex by remember { mutableStateOf(0) }

    val listItem = listOf(
        NavItem(label = "Home", icon = R.drawable.baseline_home_24),
        NavItem(label = "Search", icon = R.drawable.baseline_search_24),
        NavItem(label = "Notification", icon = R.drawable.baseline_notifications_24),
        NavItem(label = "Profile", icon = R.drawable.baseline_person_24),
    )
//    val email = activity.intent.getStringExtra("email")
//    val password = activity.intent.getStringExtra("password")


    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = White,
                    actionIconContentColor = White,
                    containerColor = Blue,
                    navigationIconContentColor = White
                ),
                title = {
                    Text("Ecommerce")
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Image(
                            painter = painterResource(R.drawable.baseline_face_24),
                            contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(painter = painterResource(R.drawable.baseline_more_horiz_24),contentDescription = null)
                    }
                    IconButton(onClick = {}) {
                        Icon(painter = painterResource(R.drawable.baseline_visibility_off_24), contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                listItem.forEachIndexed { index,item->
                    NavigationBarItem(
                        icon = {
                            Icon(painter = painterResource(item.icon),contentDescription = null)
                        },
                        label = {Text(item.label)},
                        onClick = {
                            selectedindex =index
                        },
                        selected = selectedindex == index
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when(selectedindex){
                0 -> HomeScreen(viewModel = viewModel())
                1 -> SearchScreen()
                2 -> NotificationScreen()
                3 -> ProfileScreen()
                else -> HomeScreen(viewModel = viewModel())
            }
        }
    }
}


