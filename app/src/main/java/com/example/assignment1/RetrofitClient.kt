package com.example.assignment1

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


object RetrofitClient {



    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val interfaceApi: InterfaceApi by lazy {
             Retrofit.Builder()
            .baseUrl("https://todos.simpleapi.dev")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(InterfaceApi::class.java)
    }

}