package com.owesome.data.api.dto

import com.google.gson.annotations.SerializedName

data class CompactGroupDTO(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("profile_image")
    val image: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("status")
    val status: Float
)

data class GroupListDTO(
    val groups: List<CompactGroupDTO>?
)

data class GroupDTO(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("profile_image")
    val image: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("status")
    val status: Float,

    @SerializedName("members")
    val members: List<UserDTO>,

    @SerializedName("expenses")
    val expenses: List<ExpenseDTO>
)

data class GroupResponseDTO(
    val group: GroupDTO
)

data class CreateGroupDTO(
    val description: String,
    val name: String,
    @SerializedName("profile_image")
    val profileImage: String
)

data class UpdateGroupDTO(
    @SerializedName("name")
    val name: String,
    @SerializedName("profile_image")
    val image: String,
    @SerializedName("description")
    val description: String
)

data class AddMemberDTO(
    @SerializedName("user_id")
    val userId: Int
)

