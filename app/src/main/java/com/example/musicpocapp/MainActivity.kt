package com.example.musicpocapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicpocapp.ui.theme.MusicPOCAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val navItems = listOf(
                BottomNavItem.Home,
                BottomNavItem.Search,
                BottomNavItem.Playlist
            )
            MusicPOCAppTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
                    NavigationBar {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route

                        navItems.forEach { item->
                            NavigationBarItem(
                                icon = {Icon(painter = painterResource(item.id), contentDescription = item.title)},
                                label = {Text(item.title)},
                                selected = currentRoute == item.route,
                                onClick = {
                                    navController.navigate(item.route){
                                        popUpTo(navController.graph.startDestinationId){
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }

                                }

                            )
                        }
                    }
                }) { innerPadding ->
                    NavigationGraph(innerPadding, navController)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MusicPOCAppTheme {
        Greeting("Android")
    }
}

sealed class BottomNavItem(val route: String, @DrawableRes val id:Int, val title: String) {
    data object Home : BottomNavItem("home", R.drawable.baseline_playlist_add_24,"Home")
    data object Search : BottomNavItem("search", R.drawable.baseline_playlist_add_24,"Search")
    data object Playlist : BottomNavItem("playlist", R.drawable.baseline_playlist_add_24, "Playlist")
}