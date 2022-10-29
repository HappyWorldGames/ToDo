package com.happyworldgames.todo.core

data class CardListInfo(var position: Int, var title: String, val cardInfoList: ArrayList<CardInfo> = arrayListOf())
