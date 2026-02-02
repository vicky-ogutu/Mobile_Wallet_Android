package com.example.mobilewallet

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateDeserializer : JsonDeserializer<Date> {
    private val formats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd"
    )

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Date {
        json?.asString?.let { dateString ->
            formats.forEach { format ->
                try {
                    return SimpleDateFormat(format, Locale.getDefault()).parse(dateString) ?: Date()
                } catch (e: Exception) {

                }
            }
        }
        return Date()
    }
}