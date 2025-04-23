package com.example.musicpocapp

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.example.musicpocapp.ComposeScreens.HomeScreen
import com.example.musicpocapp.ComposeScreens.PlaylistScreen
import com.example.musicpocapp.ComposeScreens.SearchScreen

@Composable
fun NavigationGraph(paddingValues: PaddingValues, navController: NavHostController)  {

    val navGraph = navController.createGraph(
        startDestination = "home"){
        composable("home"){
            HomeScreen()
        }
        composable("search"){
            SearchScreen()
        }
        composable("playlist"){
            PlaylistScreen()
        }
    }
    NavHost(navController, navGraph, Modifier.padding(paddingValues))

}