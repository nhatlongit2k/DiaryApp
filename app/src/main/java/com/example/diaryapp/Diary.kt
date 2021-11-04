package com.example.diaryapp

import java.io.Serializable
import java.util.*

class Diary(var id: Int, var title: String, var content: String, var date: Date):Serializable {
}