package com.example.assignment1

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs(context: Context) {



    val prefs: SharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)



}