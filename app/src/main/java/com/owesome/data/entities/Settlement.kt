package com.owesome.data.entities

import java.time.Instant
import java.time.OffsetDateTime

data class Settlement(
    val id: Int,
    val amount: Float,
    val payer: User,
    val receiver: User,
    val paidAt: OffsetDateTime?,
    val createdAt: OffsetDateTime
)
