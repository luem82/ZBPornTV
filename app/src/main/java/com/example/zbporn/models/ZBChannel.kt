package com.example.zbporn.models

import java.io.Serializable

data class ZBChannel(
    val title: String,
    val thumb: String,
    val count: String,
    val rate: String,
    val href: String,
    val limit: Int
) : Serializable
