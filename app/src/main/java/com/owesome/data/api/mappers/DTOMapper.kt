package com.owesome.data.api.mappers

import com.owesome.data.api.dto.CompactGroupDTO
import com.owesome.data.api.dto.ExpenseDTO
import com.owesome.data.api.dto.ExpenseShareDTO
import com.owesome.data.api.dto.GroupDTO
import com.owesome.data.api.dto.NotificationDTO
import com.owesome.data.api.dto.UserDTO
import com.owesome.data.entities.Expense
import com.owesome.data.entities.ExpenseShare
import com.owesome.data.entities.Group
import com.owesome.data.entities.GroupCompact
import com.owesome.data.entities.Notification
import com.owesome.data.entities.NotificationType
import com.owesome.data.entities.User
import com.owesome.util.ImageUtil

fun CompactGroupDTO.toCompactGroup(): GroupCompact {
    return GroupCompact(
        id = this.id,
        name = this.name,
        description = this.description,
        status = this.status,
        image = ImageUtil.decodeBase64ToImageBitmap(this.image)
    )
}

fun GroupDTO.toGroup(): Group {
    return Group(
        id = this.id,
        name = this.name,
        description = this.description,
        users = this.members.map {
            it.toUser()
        },
        expenses = this.expenses.map { expense ->
            expense.toExpense(this.id)
        },
        status = this.status,
        image = ImageUtil.decodeBase64ToImageBitmap(this.image)
    )
}

fun ExpenseDTO.toExpense(groupId: String): Expense {
    return Expense(
        id = this.id,
        amount = this.amount,
        description = this.description,
        groupId = groupId,
        paidBy = this.paidBy.toUser(),
        split = this.expenseShares.map {
            it.toExpenseShare(this.id)
        },
        status = this.status
    )
}

fun ExpenseShareDTO.toExpenseShare(expenseId: Int): ExpenseShare {
    return ExpenseShare(
        id = this.id,
        expenseId = expenseId,
        owedBy = this.user.toUser(),
        amount = this.amount
    )
}

fun UserDTO.toUser(): User {
    return User(
        id = this.id,
        username = this.username,
        email = this.email,
        phone = this.phone
    )
}

fun NotificationDTO.toNotification(): Notification {
    return Notification(
        message = this.message,
        messageType = NotificationType.Info
    )
}