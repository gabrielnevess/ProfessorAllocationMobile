package com.example.professorallocationmobile.config

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.sql.Time

class SqlTimeDeserializer : JsonDeserializer<Time> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Time {
        return Time.valueOf(json.asString)
    }
}