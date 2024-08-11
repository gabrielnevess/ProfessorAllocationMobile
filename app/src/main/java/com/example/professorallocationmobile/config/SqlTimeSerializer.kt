package com.example.professorallocationmobile.config

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.sql.Time

class SqlTimeSerializer : JsonSerializer<Time> {
    override fun serialize(
        src: Time?,
        typeOfSrc: Type?,
        context: com.google.gson.JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.toString())
    }
}