package com.example.zbporn.models

import java.io.Serializable

data class ZBVideo(
    var href: String,
    var title: String,
    var thumb: String,
    var duration: String,
    var date: String,
    var likes: String,
    var preview: String,
    var limit: Int
) : Serializable
