package com.happyworldgames.todo.model

data class CardInfo(override var id: String, override var position: Int, override var name: String,
                    var description: String): InfoInterface