package tech.subly.nisaba.team

import jakarta.transaction.Transactional
import org.keycloak.representations.idm.GroupRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import tech.subly.nisaba.keycloak.KeycloakClientService
import tech.subly.nisaba.project.ProjectTeamRepository


@RestController
@RequestMapping("/api/teams")
class TeamsController(
    private val keycloakClientService: KeycloakClientService,
    private val projectTeamRepository: ProjectTeamRepository
) {

    @GetMapping
    @PreAuthorize("hasAuthority('per:team:view')")
    fun fetchTeams(): List<GroupRepresentation> {
        return keycloakClientService.getGroups()
            .sortedBy { it.name }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('per:team:create')")
    fun createTeam(@RequestBody group: GroupRepresentation?): ResponseEntity<GroupRepresentation> {
        val newGroup: GroupRepresentation = keycloakClientService.createGroup(group)
        return ResponseEntity<GroupRepresentation>(newGroup, HttpStatus.CREATED)
    }

    @GetMapping("/{teamId}")
    @PreAuthorize("hasAuthority('per:team:view')")
    fun getTeamById(@PathVariable teamId: String): ResponseEntity<GroupRepresentation> {
        val team = keycloakClientService.getGroupById(teamId)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(team)
    }

    @PostMapping("{teamId}/members")
    @PreAuthorize("hasAuthority('per:team-member:add')")
    fun addUserToTeam(@PathVariable teamId: String, @RequestBody members: List<String>): ResponseEntity<Void> {
        members.forEach { member ->
            keycloakClientService.addUserToGroup(teamId, member)
        }
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{teamId}/members/{userId}") // Updated path
    @PreAuthorize("hasAuthority('per:team-member:remove')")
    fun removeUserFromTeam(@PathVariable teamId: String, @PathVariable userId: String): ResponseEntity<Void> {
        keycloakClientService.removeUserFromGroup(teamId, userId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{teamId}/members")
    @PreAuthorize("hasAuthority('per:team-member:view')")
    fun getGroupMembers(@PathVariable teamId: String): ResponseEntity<List<UserRepresentation>> {
        val members = keycloakClientService.getGroupMembers(teamId)
        return ResponseEntity.ok(members)
    }

    @DeleteMapping("/{teamId}")
    @Transactional
    @PreAuthorize("hasAuthority('per:team:delete')")
    fun deleteGroup(@PathVariable teamId: String): ResponseEntity<Void> {
        keycloakClientService.deleteGroupById(teamId)
        projectTeamRepository.deleteByTeamId(teamId)
        return ResponseEntity.noContent().build()
    }

}
