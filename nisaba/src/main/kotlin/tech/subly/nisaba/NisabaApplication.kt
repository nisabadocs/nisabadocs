package tech.subly.nisaba

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(CorsProperties::class, KeycloakClientProperties::class)
class NisabaApplication

fun main(args: Array<String>) {
    runApplication<NisabaApplication>(*args)
}
