package com.example.zbporn.models

import java.io.Serializable

data class ZBModel(
    var href: String,
    var name: String,
    var thumb: String,
    var likes: String,
    var videoCount: String,
    var photoCount: String,
    var flag: String,
    var limit: Int
) : Serializable
