package com.happyworldgames.todo.model

import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.collections.ArrayList

@Serializable
data class CardInfo(
    override var id: String = UUID.randomUUID().toString(),                 // card folder name
    override var position: Int = -1,                                        // card position
    override var name: String = "Null",                                     // card name
    var description: String = "",                                           // card description
    val tagList: ArrayList<TagItem> = arrayListOf(),                        // tag list
    val checkList: ArrayList<CheckItem> = arrayListOf()                     // task check list
    ): InfoInterface

@Serializable
data class CheckItem(var name: String, var boolean: Boolean = false)