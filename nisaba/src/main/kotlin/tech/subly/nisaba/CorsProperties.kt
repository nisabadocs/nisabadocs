package tech.subly.nisaba

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cors")
data class CorsProperties(
    val allowedOrigins: List<String> = listOf(),
    val allowedMethods: List<String> = listOf(),
    val allowedHeaders: List<String> = listOf()
)
