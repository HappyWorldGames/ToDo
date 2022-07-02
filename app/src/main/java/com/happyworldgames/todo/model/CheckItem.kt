package com.happyworldgames.todo.model

import kotlinx.serialization.Serializable

@Serializable
data class CheckItem(var name: String, var boolean: Boolean = false)