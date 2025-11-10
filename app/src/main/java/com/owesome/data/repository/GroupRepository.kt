package com.owesome.data.repository

import com.owesome.data.entities.Expense
import com.owesome.data.entities.ExpenseCreate
import com.owesome.data.entities.ExpenseShare
import com.owesome.data.entities.Group
import com.owesome.data.entities.GroupCompact
import com.owesome.data.entities.User
import kotlinx.coroutines.delay

interface GroupRepository {
    suspend fun getAllGroups(): List<GroupCompact>
    suspend fun getGroup(groupId: String): Group?

    suspend fun createGroup(name: String, description: String)
    suspend fun addUser(groupId: Int, userId: Int)

    suspend fun addExpense(expense: ExpenseCreate)
}

class GroupRepositoryImpl : GroupRepository {
    override suspend fun getGroup(groupId: String): Group {
        delay(200)

        val u1 = User(
            0,
            "Bob",
            "bob@email.com",
            12345678
        )

        val u2 = User(
            1,
            "Alice",
            "alice@email.com",
            12345678
        )

        val es1 = ExpenseShare(
            0,
            0,
            u1,
            500
        )

        val es2 = ExpenseShare(
            1,
            1,
            u2,
            500
        )

        val e1 = Expense(
            0,
            1000f,
            "Drinks",
            0,
            u2,
            listOf(es1),
            -500f
        )

        val e2 = Expense(
            1,
            1200f,
            "Hotel",
            0,
            u1,
            listOf(es2),
            500f
        )

        val g1 = Group(
            0,
            "Vacation to Prague",
            "",
            listOf(u1, u2),
            listOf(e1, e2),
            0f
        )

        return g1
    }

    override suspend fun getAllGroups(): List<GroupCompact> {
        return mutableListOf(
            GroupCompact(0, "Vacation to Prague", "", -400),
            GroupCompact(1, "Household", "", 2500),
        )
    }

    override suspend fun createGroup(name: String, description: String) {
        TODO("Not yet implemented")
    }

    override suspend fun addUser(groupId: Int, userId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun addExpense(expense: ExpenseCreate) {
        TODO("Not yet implemented")
    }
}