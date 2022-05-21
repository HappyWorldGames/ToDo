package com.happyworldgames.todo.model

data class ListInfo(override var id: String, override var position: Int, override var name: String,
               val cards: ArrayList<CardInfo> = arrayListOf()): InfoInterface