package com.owesome.data.entities

data class Group(val id: Int, val name: String, val description: String, val users: List<User>, val expenses: List<Expense>)
