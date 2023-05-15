package com.example.myedition.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myedition.utilities.Constance.Companion.Class_name
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = Class_name
)
@Parcelize
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
):Parcelable