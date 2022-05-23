package com.happyworldgames.todo.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ListInfo(
    override var id: String,
    override var position: Int,
    override var name: String,
    @Transient val cards: ArrayList<CardInfo> = arrayListOf()   // list cards
): InfoInterface