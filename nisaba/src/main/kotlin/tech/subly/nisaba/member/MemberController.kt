package tech.subly.nisaba.member

import jakarta.transaction.Transactional
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import tech.subly.nisaba.keycloak.KeycloakClientService
import tech.subly.nisaba.project.ProjectMemberRepository

@RestController
@RequestMapping("/api/members")
class MemberController(
    private val keycloakClientService: KeycloakClientService,
    private val projectMemberRepository: ProjectMemberRepository
) {

    @GetMapping
    @PreAuthorize("hasAuthority('per:member:view')")
    fun getAllUsers(): ResponseEntity<List<MemberDTO>> {
        val users = this.keycloakClientService.getUsersWithRealmRoles()
            .filter { StringUtils.hasText(it.email) }
            .sortedBy { it.email }
        return ResponseEntity.ok(users)
    }

    @PostMapping
    @PreAuthorize("hasAuthority('per:member:create')")
    fun createUser(@RequestBody user: UserRepresentation): ResponseEntity<UserRepresentation> {
        val newUser = keycloakClientService.createMember(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser)
    }

    @DeleteMapping("/{userId}")
    @Transactional
    @PreAuthorize("hasAuthority('per:member:delete')")
    fun deleteUser(@PathVariable userId: String): ResponseEntity<Void> {
        projectMemberRepository.deleteByUserId(userId)
        keycloakClientService.deleteUserById(userId)
        return ResponseEntity.noContent().build()  // 204 No Content on success
    }

    @PostMapping("/{userId}/roles/{roleName}")
    @PreAuthorize("hasAuthority('per:member:assignRole')")
    fun assignRoleToUser(@PathVariable userId: String, @PathVariable roleName: String) {
        keycloakClientService.assignRealmRoleToUser(userId, roleName)
    }
}
