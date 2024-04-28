package tech.subly.nisaba.project

import jakarta.validation.Valid
import org.keycloak.representations.idm.GroupRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/api/projects")
class ProjectController(
    private val projectService: ProjectService,
    private val projectMemberService: ProjectMemberService,
    private val projectTeamService: ProjectTeamService
) {

    @PostMapping
    @PreAuthorize("hasAuthority('per:project:create')")
    fun createProject(@Valid @RequestBody projectRequestDTO: ProjectCreationRequestDTO): ResponseEntity<Map<String, Any>> {
        val project = projectService.createProject(projectRequestDTO)
        return ResponseEntity.ok(mapOf("project" to project))
    }

    @DeleteMapping("/{projectId}")
    @PreAuthorize("hasAuthority('per:project:delete')")
    fun deleteProject(@PathVariable projectId: UUID): ResponseEntity<Void> {
        projectService.deleteProject(projectId)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{projectId}")
    @PreAuthorize("hasAuthority('per:project:update')")
    fun updateProject(
        @PathVariable projectId: UUID,
        @Valid @RequestBody projectRequestDTO: ProjectRequestDTO
    ): ResponseEntity<Project> {
        val updatedProject = projectService.updateProject(projectId, projectRequestDTO)
        return ResponseEntity.ok(updatedProject)
    }


    @GetMapping
    @PreAuthorize("hasAuthority('per:project:view')")
    fun getAllProjects(): ResponseEntity<List<Project>> {
        val projects = projectService.findAllProjects()
        return ResponseEntity.ok(projects)
    }

    @GetMapping("/public")
    fun getPublicProjects(): ResponseEntity<List<Project>> {
        val projects = projectService.findPublicProjects()
        return ResponseEntity.ok(projects)
    }

    @GetMapping("/{projectId}")
    @PreAuthorize("hasAuthority('per:project:view')")
    fun getProjectById(@PathVariable projectId: UUID): ResponseEntity<Project> {
        val project = projectService.getProjectById(projectId) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(project)
    }

    @GetMapping("/{projectId}/teams")
    @PreAuthorize("hasAuthority('per:project-team:view')")
    fun getProjectTeams(@PathVariable projectId: UUID): ResponseEntity<List<GroupRepresentation>> {
        return ResponseEntity.ok(projectTeamService.getProjectTeams(projectId))
    }

    @GetMapping("/{projectId}/members")
    @PreAuthorize("hasAuthority('per:project-member:view')")
    fun getProjectMembers(@PathVariable projectId: UUID): ResponseEntity<List<UserRepresentation>> {
        return ResponseEntity.ok(projectMemberService.getProjectMembers(projectId))
    }

    @PostMapping("/{projectId}/teams")
    @PreAuthorize("hasAuthority('per:project-team:assign')")
    fun assignTeamsToProject(
        @PathVariable projectId: UUID,
        @RequestBody teamIds: List<String>
    ): ResponseEntity<Void> {
        projectService.assignTeamsToProject(projectId, teamIds)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{projectId}/members")
    @PreAuthorize("hasAuthority('per:project-member:assign')")
    fun assignMembersToProject(
        @PathVariable projectId: UUID,
        @RequestBody userIds: List<String>
    ): ResponseEntity<Void> {
        projectService.assignMembersToProject(projectId, userIds)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{projectId}/teams/{teamId}")
    @PreAuthorize("hasAuthority('per:project-team:remove')")
    fun removeTeamFromProject(
        @PathVariable projectId: UUID?,
        @PathVariable teamId: String?
    ): ResponseEntity<Void> {
        projectTeamService.removeTeamFromProject(projectId, teamId)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{projectId}/members/{memberId}")
    @PreAuthorize("hasAuthority('per:project-member:remove')")
    fun removeMemberFromProject(
        @PathVariable projectId: UUID?,
        @PathVariable memberId: String?
    ): ResponseEntity<Void> {
        projectMemberService.removeMemberFromProject(projectId, memberId)
        return ResponseEntity.noContent().build()
    }
}
