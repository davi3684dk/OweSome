package com.owesome.data.repository

import com.owesome.data.entities.ExpenseCreate

interface GroupRepository {
    fun createGroup(name: String, description: String)
    fun addUser(groupId: Int, userId: Int)

    fun addExpense(expense: ExpenseCreate)
}

class GroupRepositoryImpl : GroupRepository {
    override fun createGroup(name: String, description: String) {
        TODO("Not yet implemented")
    }

    override fun addUser(groupId: Int, userId: Int) {
        TODO("Not yet implemented")
    }

    override fun addExpense(expense: ExpenseCreate) {
        TODO("Not yet implemented")
    }
}