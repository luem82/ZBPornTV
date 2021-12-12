package com.example.zbporn.models

import java.io.Serializable

data class ZBAlbum(
    val title: String,
    val thumb: String,
    val count: String,
    val date: String,
    val href: String,
    val limit: Int
) : Serializable
