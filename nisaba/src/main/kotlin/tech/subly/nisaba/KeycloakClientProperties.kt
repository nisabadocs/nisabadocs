package tech.subly.nisaba

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "keycloak")
class KeycloakClientProperties {
    lateinit var authServerUrl: String
    lateinit var realm: String
    lateinit var clientId: String
    lateinit var clientSecret: String
}
