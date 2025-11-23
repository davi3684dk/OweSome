package com.owesome.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.owesome.ui.theme.OweSomeTheme

@Composable
fun SplashScreen(

) {
    OweSomeTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "OweSome",
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.padding(bottom = 50.dp)
                )

                CircularProgressIndicator(
                    modifier = Modifier.size(128.dp)
                )
            }
        }
    }
}