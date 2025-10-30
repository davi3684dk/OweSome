package com.owesome.ui.groups

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel

@Composable
fun Groups(viewModel: GroupViewModel = koinViewModel()) {
    Text(
        viewModel.getText()
    )
}