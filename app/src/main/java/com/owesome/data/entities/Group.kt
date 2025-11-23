package com.owesome.data.entities

import androidx.compose.ui.graphics.ImageBitmap

data class Group(
    val id: String,
    val name: String,
    val description: String,
    val users: List<User>,
    val expenses: List<Expense>,
    val status: Float,
    val image: ImageBitmap?,
    val settlements: List<Settlement>,
    val owner: User
)

data class GroupCompact(
    val id: String,
    val name: String,
    val description: String,
    val status: Number,
    val image: ImageBitmap?
)


