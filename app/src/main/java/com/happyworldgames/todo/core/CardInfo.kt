package com.happyworldgames.todo.core

@kotlinx.serialization.Serializable
data class CardInfo(var position: Int, var title: String, var description: String, val checkBoxList: ArrayList<CardCheckBoxItem> = arrayListOf())

@kotlinx.serialization.Serializable
data class CardCheckBoxItem(var position: Int, var title: String, var value: Boolean)
