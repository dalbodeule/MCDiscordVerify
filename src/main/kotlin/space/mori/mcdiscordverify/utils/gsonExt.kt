package space.mori.mcdiscordverify.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting().create()
val gson = Gson()

fun Any?.serializeJSON(): String = gsonBuilder.toJson(this)

fun <T>parseJSON(json: String, clazz: Class<T>): T = gson.fromJson(json, clazz)