package com.example.musicpocapp.ComposeScreens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.musicpocapp.utilities.StaticData

@Composable
fun HomeScreen(){
//    Greeting("Android")

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxHeight().background(Color.Black).verticalScroll(ScrollState(0), true)) {
        StaticData.songs.forEach {

            ConstraintLayout(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                val (title, artist, album, duration) = createRefs()
                Text(it.title, modifier = Modifier.constrainAs(title){
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }, Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(it.artist, modifier = Modifier.constrainAs(artist){
                    start.linkTo(title.start)
                    top.linkTo(title.bottom, margin = 5.dp)
                }, Color.White)
                Text(it.album, modifier = Modifier.constrainAs(album){
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }, Color.White)
                Text(it.duration, modifier = Modifier.constrainAs(duration){
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }, Color.White)
            }



        }



    }
}