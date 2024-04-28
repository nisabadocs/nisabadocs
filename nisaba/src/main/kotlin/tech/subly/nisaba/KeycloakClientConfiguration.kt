package tech.subly.nisaba

import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KeycloakClientConfiguration {

    @Bean
    fun keycloak(props: KeycloakClientProperties): Keycloak = KeycloakBuilder.builder()
        .serverUrl(props.authServerUrl)
        .realm(props.realm)
        .clientId(props.clientId)
        .clientSecret(props.clientSecret)
        .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
        .build()

}
