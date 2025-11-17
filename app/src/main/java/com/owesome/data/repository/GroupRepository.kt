package com.owesome.data.repository

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.asImageBitmap
import com.owesome.data.api.GroupApiService
import com.owesome.data.api.dto.AddMemberDTO
import com.owesome.data.api.dto.CreateGroupDTO
import com.owesome.data.entities.Expense
import com.owesome.data.entities.ExpenseCreate
import com.owesome.data.entities.ExpenseShare
import com.owesome.data.entities.Group
import com.owesome.data.entities.GroupCompact
import com.owesome.data.entities.User
import com.owesome.util.ImageUtil
import kotlinx.coroutines.delay
import java.lang.Exception

interface GroupRepository {
    suspend fun getAllGroups(): List<GroupCompact>
    suspend fun getGroup(groupId: String): Group?

    suspend fun createGroup(name: String, description: String, users: List<User>, imageBase64: String): Group?
    suspend fun addUser(groupId: String, userId: Int)

    suspend fun addExpense(expense: ExpenseCreate)
    suspend fun updateGroup(groupId: String, name: String, description: String, users: List<User>, imageBase64: String): Group?
}

class GroupRepositoryImpl(
    val groupApiService: GroupApiService
) : GroupRepository {
    override suspend fun getGroup(groupId: String): Group? {
        delay(200)

        val response = groupApiService.getGroup(groupId)
        if (response != null) {

            return Group(
                id = response.id,
                name = response.name,
                description = response.description,
                users = response.members.map {
                    User(
                        id = it.id,
                        username = it.username,
                        email = it.email,
                        phone = it.phone
                    )
                },
                expenses = listOf(),
                status = response.status,
                image = ImageUtil.decodeBase64ToImageBitmap(response.image)
            )

        } else {
            return null
        }

        /*val u1 = User(
            0,
            "Bob",
            "bob@email.com",
            "12345678"
        )

        val u2 = User(
            1,
            "Alice",
            "alice@email.com",
            "12345678"
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
            "0",
            "Vacation to Prague",
            "",
            listOf(u1, u2),
            listOf(e1, e2),
            0f,
            null
        )

        return g1*/
    }

    override suspend fun getAllGroups(): List<GroupCompact> {
        delay(2000)

        val response = groupApiService.getGroups()
        return response.groups?.map {
            GroupCompact(
                id = it.id,
                name = it.name,
                description = it.description,
                status = it.status,
                image = ImageUtil.decodeBase64ToImageBitmap(it.image)
            )
        } ?: listOf()

        /*
        return mutableListOf(
            GroupCompact(0, "Vacation to Prague", "", -400, null),
            GroupCompact(1, "Household", "", 2500, null),
        )*/
    }

    override suspend fun createGroup(name: String, description: String, users: List<User>, imageBase64: String): Group? {

        val response = groupApiService.createGroup(
            CreateGroupDTO(
                description = description,
                name = name,
                profileImage = imageBase64
            )
        )

        if (response?.group != null) {
            //Add users
            for (user in users) {
                try {
                    groupApiService.addMember(response.group.id, AddMemberDTO(user.id))
                } catch (e: Exception) {
                    println(e.localizedMessage)
                }
            }
        }

        return Group(
            id = "0",
            name = name,
            description = description,
            users = users,
            expenses = listOf(),
            status = 0f,
            null
        )
    }

    override suspend fun updateGroup(
        groupId: String,
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

    override suspend fun addUser(groupId: String, userId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun addExpense(expense: ExpenseCreate) {
        TODO("Not yet implemented")
    }
}