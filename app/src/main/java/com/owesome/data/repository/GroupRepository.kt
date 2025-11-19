package com.owesome.data.repository

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.asImageBitmap
import coil.network.HttpException
import com.owesome.data.api.GroupApiService
import com.owesome.data.api.dto.AddMemberDTO
import com.owesome.data.api.dto.CreateGroupDTO
import com.owesome.data.api.dto.UpdateGroupDTO
import com.owesome.data.api.mappers.toCompactGroup
import com.owesome.data.api.mappers.toGroup
import com.owesome.data.entities.Expense
import com.owesome.data.entities.ExpenseCreate
import com.owesome.data.entities.ExpenseShare
import com.owesome.data.entities.Group
import com.owesome.data.entities.GroupCompact
import com.owesome.data.entities.User
import com.owesome.util.ImageUtil
import kotlinx.coroutines.delay
import java.lang.Exception
import kotlin.math.exp

interface GroupRepository {
    suspend fun getAllGroups(): List<GroupCompact>
    suspend fun getGroup(groupId: String): Group?

    suspend fun createGroup(name: String, description: String, users: List<User>, imageBase64: String): Group?
    suspend fun addUser(groupId: String, userId: Int)

    suspend fun addExpense(expense: ExpenseCreate)
    suspend fun updateGroup(groupId: String, name: String, description: String, addedUsers: List<Int>, removedUsers: List<Int>, imageBase64: String): Group?
}

class GroupRepositoryImpl(
    val groupApiService: GroupApiService
) : GroupRepository {
    override suspend fun getGroup(groupId: String): Group? {
        val response = groupApiService.getGroup(groupId)
        return if (response.isSuccessful)
            response.body()?.toGroup()
        else
            null
    }

    override suspend fun getAllGroups(): List<GroupCompact> {
        val response = groupApiService.getGroups()
        return if (response.isSuccessful)
            response.body()?.groups?.map {
                it.toCompactGroup()
            } ?: listOf()
        else
            return listOf()
    }

    override suspend fun createGroup(name: String, description: String, users: List<User>, imageBase64: String): Group? {

        val response = groupApiService.createGroup(
            CreateGroupDTO(
                description = description,
                name = name,
                profileImage = imageBase64
            )
        )

        if (response.isSuccessful && response.body()?.group != null) {
            val group = response.body()!!.group

            //Add users
            for (user in users) {
                try {
                    groupApiService.addMember(group.id, AddMemberDTO(user.id))
                } catch (e: Exception) {
                    println(e.localizedMessage)
                }
            }

            return Group(
                id = group.id,
                name = group.name,
                description = group.description,
                users = users,
                expenses = listOf(),
                status = 0f,
                image = ImageUtil.decodeBase64ToImageBitmap(group.image)
            )
        }

        return null
    }

    override suspend fun updateGroup(
        groupId: String,
        name: String,
        description: String,
        addedUsers: List<Int>,
        removedUsers: List<Int>,
        imageBase64: String
    ): Group? {

        val response = groupApiService.updateGroup(groupId, UpdateGroupDTO(
            name = name,
            image = imageBase64,
            description = description
        ))

        for (user in addedUsers) {
            groupApiService.addMember(groupId, AddMemberDTO(user))
        }

        for (user in removedUsers) {
            groupApiService.removeMember(groupId, AddMemberDTO(user))
        }

        return if (response.isSuccessful && response.body()?.group != null) {
            response.body()!!.group.toGroup()
        } else {
            null
        }

    }

    override suspend fun addUser(groupId: String, userId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun addExpense(expense: ExpenseCreate) {
        TODO("Not yet implemented")
    }
}