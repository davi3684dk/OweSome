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
import kotlin.math.exp

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
                expenses = response.expenses.map { expense ->
                    Expense(
                        id = expense.id,
                        amount = expense.amount,
                        description = expense.description,
                        groupId = response.id,
                        paidBy = User(
                            id = expense.paidBy.id,
                            username = expense.paidBy.username,
                            email = expense.paidBy.email,
                            phone = expense.paidBy.phone
                        ),
                        split = expense.expenseShares.map {
                            ExpenseShare(
                                id = it.id,
                                expenseId = expense.id,
                                owedBy = User(
                                    id = it.user.id,
                                    username = it.user.username,
                                    email = it.user.email,
                                    phone = it.user.phone
                                ),
                                amount = it.amount
                            )
                        },
                        status = expense.status
                    )
                },
                status = response.status,
                image = ImageUtil.decodeBase64ToImageBitmap(response.image)
            )

        } else {
            return null
        }
    }

    override suspend fun getAllGroups(): List<GroupCompact> {
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

            return Group(
                id = response.group.id,
                name = response.group.name,
                description = response.group.description,
                users = response.group.members.map {
                    User(
                        id = it.id,
                        username = it.username,
                        email = it.email,
                        phone = it.phone
                    )
                },
                expenses = listOf(),
                status = 0f,
                image = ImageUtil.decodeBase64ToImageBitmap(response.group.image)
            )
        }

        return null
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