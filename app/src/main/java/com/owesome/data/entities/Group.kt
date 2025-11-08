package com.owesome.data.entities

data class Group(val id: Int, val name: String, val description: String, val users: List<User>, val expenses: List<Expense>, val status: Float)

data class GroupCompact(val id: Int, val name: String, val description: String, val status: Number)
