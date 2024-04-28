package tech.subly.nisaba

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import java.util.stream.Collectors


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    @Value("\${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    lateinit var jwkSetUri: String

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/api/project-versions/ci/**",
                        "/api/project-docs",
                        "/api/project-versions",
                        "/api/settings/logo",
                        "/api/settings/viewer",
                        "/api/settings/landing-page",
                        "/api/projects/public"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .csrf { csrf ->
                csrf.ignoringRequestMatchers(
                    "/api/project-versions/ci/**",
                    "/api/project-docs",
                    "/api/project-versions",
                    "/api/settings/logo",
                    "/api/settings/viewer",
                    "/api/settings/landing-page",
                    "/api/projects/public"
                )
            } // Disable CSRF
            .oauth2ResourceServer { oauth2 ->
                oauth2
                    .jwt {
                        it.jwkSetUri(jwkSetUri)
                        it.jwtAuthenticationConverter(jwtAuthenticationConverterForKeycloak())
                    }
            }
        return http.build()
    }

    @Bean
    fun jwtAuthenticationConverterForKeycloak(): JwtAuthenticationConverter {
        val jwtGrantedAuthoritiesConverter: Converter<Jwt, Collection<GrantedAuthority>> = Converter { jwt ->
            val realmAccess = jwt.getClaim<Map<String, List<String>>>("realm_access")
            val allRoles = realmAccess["roles"] ?: emptyList() // Null safe access

            // Separate permissions and roles
            val roles = allRoles.filter { it.startsWith("ROLE_") }
            val permissions = allRoles.filter { it.startsWith("per:") }

            val grantedAuthorities = (roles + permissions).stream()
                .map { roleName -> SimpleGrantedAuthority(roleName) }
                .collect(Collectors.toList<GrantedAuthority>())

            grantedAuthorities
        }

        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter)
        return jwtAuthenticationConverter
    }

    @Bean
    fun encoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

}
