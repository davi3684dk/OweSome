package com.owesome.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.owesome.ui.viewmodels.GroupViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun Groups(viewModel: GroupViewModel = koinViewModel()) {
    Text(
        viewModel.getText()
    )
}