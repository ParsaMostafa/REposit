package com.example.myedition.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myedition.utilities.Constance.Companion.Class_name

@Entity(
    tableName = Class_name
)
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id:Int?=null,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String,
    val urlToImage: String?
):java.io.Serializable