package com.owesome.data.repository

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.ui.graphics.asImageBitmap
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

    suspend fun createGroup(name: String, description: String, users: List<User>): Group?
    suspend fun addUser(groupId: Int, userId: Int)

    suspend fun addExpense(expense: ExpenseCreate)
    suspend fun updateGroup(groupId: Int, name: String, description: String, users: List<User>, imageBase64: String): Group?
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
            0f,
            null
        )

        return g1
    }

    override suspend fun getAllGroups(): List<GroupCompact> {
        delay(200)

        return mutableListOf(
            GroupCompact(0, "Vacation to Prague", "", -400, null),
            GroupCompact(1, "Household", "", 2500, null),
        )
    }

    override suspend fun createGroup(name: String, description: String, users: List<User>): Group? {
        return Group(
            id = 0,
            name = name,
            description = description,
            users = users,
            expenses = listOf(),
            status = 0f,
            null
        )
    }

    override suspend fun updateGroup(
        groupId: Int,
        name: String,
        description: String,
        users: List<User>,
        imageBase64: String
    ): Group? {
        // Source - https://stackoverflow.com/a
        // Posted by jagadishlakkurcom jagadishlakk
        // Retrieved 2025-11-15, License - CC BY-SA 4.0

        val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)


        return Group(
            id = groupId,
            name = name,
            description = description,
            users = users,
            expenses = listOf(),
            status = 0f,
            image = bitmap.asImageBitmap()
        )
    }

    override suspend fun addUser(groupId: Int, userId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun addExpense(expense: ExpenseCreate) {
        TODO("Not yet implemented")
    }
}