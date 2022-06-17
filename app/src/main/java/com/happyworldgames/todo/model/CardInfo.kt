package com.happyworldgames.todo.model

import kotlinx.serialization.Serializable

@Serializable
data class CardInfo(
    override var id: String,                // card folder name
    override var position: Int,             // card position
    override var name: String,              // card name
    var description: String,                // card description
    val tagList: ArrayList<TagItem>,        // tag list
    val checkList: ArrayList<CheckItem>     // task check list
    ): InfoInterface

@Serializable
data class TagItem(var name: String, var color: Int)
@Serializable
data class CheckItem(var name: String, var boolean: Boolean = false)