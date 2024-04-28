package tech.subly.nisaba

import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class UserProvider {

    fun getUserDetailsOrThrow(): CustomUserDetails {
        return getUserDetails().orElseThrow {
            ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "User not authenticated"
            )
        }
    }

    fun getUserDetails(): Optional<CustomUserDetails> {
        val authentication = SecurityContextHolder.getContext().authentication ?: return Optional.empty()
        val principal: Any = authentication.principal ?: return Optional.empty()

        if (principal is String && principal == "anonymousUser") {
            return Optional.empty()
        }

        if (principal !is Jwt) {
            return Optional.empty()
        }
        val jwtToken: Jwt = principal
        val email: String = jwtToken.claims.get("email").toString()
        val id: String = jwtToken.claims.get("sub").toString()
        val roles = jwtToken.claims["realm_access"]?.let { it as Map<*, *> }?.get("roles") as List<String>? ?: listOf()

        val userDetails = CustomUserDetails(email, id, roles)

        return Optional.of(userDetails)
    }

    class CustomUserDetails(val email: String, val id: String, val roles: List<String>)
}