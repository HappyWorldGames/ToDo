package com.happyworldgames.todo.model

import android.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
data class TagItem(var name: String = "", var color: Int = Color.GREEN)


