package tech.subly.nisaba.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.time.Instant
class InstantSerializer : JsonSerializer<Instant>() {
    override fun serialize(value: Instant, generator: JsonGenerator, serializers: SerializerProvider) {
        generator.writeString(value.toString())
    }
}