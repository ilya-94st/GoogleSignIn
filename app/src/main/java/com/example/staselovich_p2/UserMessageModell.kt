package com.example.staselovich_p2

data class UserMessageModell(val data: String, val from: String, val subject: String, val atachmenId: String, val messageId: String, val filename: String)
    object UserMessagesModelClass {
        var dataObject: MutableList<UserMessageModell> = mutableListOf()
    }
