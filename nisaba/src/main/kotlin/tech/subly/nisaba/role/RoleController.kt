package tech.subly.nisaba.role

import org.keycloak.representations.idm.RoleRepresentation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tech.subly.nisaba.keycloak.KeycloakClientService

@RestController
@RequestMapping("/api/roles")
class RoleController(
    private val  keycloakClientService: KeycloakClientService
) {
    @GetMapping
    fun getAllRoles(): List<RoleRepresentation> {
        return keycloakClientService.listFilteredRealmRoles()
    }

}
