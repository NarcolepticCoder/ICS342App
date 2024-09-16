package com.example.assignment1

import com.squareup.moshi.Json


data class Todo(@Json(name = "description")val description: String,@Json(name = "completed")var completed: Boolean,@Json(name = "id") val id: String)
data class TodoResponse(@Json(name = "description")val description: String,@Json(name = "id") val id: String,@Json(name = "completed")var completed: Int)
data class NewUser(@Json(name = "id") val user_id: String,@Json(name = "token") val token: String)