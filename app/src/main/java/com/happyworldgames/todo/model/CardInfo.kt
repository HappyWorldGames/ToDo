package com.happyworldgames.todo.model

import kotlinx.serialization.Serializable

@Serializable
data class CardInfo(
    override var id: String,
    override var position: Int,
    override var name: String,
    var description: String         // card description
    ): InfoInterface