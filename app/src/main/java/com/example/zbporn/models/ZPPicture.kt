package com.example.zbporn.models

import java.io.Serializable

data class ZPPicture(
    val id: String,
    val title: String,
    val name: String,
    val small: String,
    val big: String
) : Serializable
