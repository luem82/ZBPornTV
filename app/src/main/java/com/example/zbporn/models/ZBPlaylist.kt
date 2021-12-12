package com.example.zbporn.models

import java.io.Serializable

data class ZBPlaylist(
    val title: String,
    val href: String,
    val thumb: String,
    val prevew1: String,
    val preview2: String,
    val preview3: String,
    val count: String,
    val limit: Int
) : Serializable
