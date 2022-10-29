package com.happyworldgames.todo.core

@kotlinx.serialization.Serializable
data class CardInfo(var position: Int, var title: String, var description: String)
