package com.example.assignment1


import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface InterfaceApi {
    @GET("/api/users/{user_id}/todos")
    suspend fun getTodos(
        @Path("user_id")userid: String, @Header("Authorization") bearerToken: String, @Query("apikey") apiKey: String
    ): List<TodoResponse>

    @POST("/api/users/{user_id}/todos")
    suspend fun createTodos(
        @Path("user_id")userid: String, @Header("Authorization") bearerToken: String,@Query("apikey") apiKey: String, @Body todo:Todo
    ): Todo

    @PUT("/api/users/{user_id}/todos/{id}")
    suspend fun editTodos(
        @Path("user_id")userid: String,@Header("Authorization") bearerToken: String, @Path("id") id: String, @Query("apikey") apiKey: String,  @Body todo:Todo
    ): Todo

    @POST("/api/users/register")
    suspend fun createUser(
        @Query("apikey") apiKey: String,@Body createUser: UserInfoRequest
    ): NewUser

    @POST("/api/users/login")
    suspend fun loginUser(
        @Query("apikey") apiKey: String,@Body createUser: UserInfoRequest
    ): NewUser



}