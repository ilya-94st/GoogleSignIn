package com.example.staselovich_p2.DataBase

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: String,
    val subject: String,
    val from: String,
    val atachmenId: String,
    val messageId: String
)
