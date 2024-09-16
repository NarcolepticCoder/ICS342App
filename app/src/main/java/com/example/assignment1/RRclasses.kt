package com.example.assignment1

import com.squareup.moshi.Json



data class UserInfoRequest(
    @Json(name = "name") val name: String?,
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)
