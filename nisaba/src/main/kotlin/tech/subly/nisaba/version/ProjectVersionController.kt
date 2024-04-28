package tech.subly.nisaba.version

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import tech.subly.nisaba.project.ProjectService
import java.util.*

@RestController
@RequestMapping("/api/project-versions")
class ProjectVersionController(
    private val projectVersionService: ProjectVersionService,
    private val projectService: ProjectService
) {

    @PostMapping
    @PreAuthorize("hasAuthority('per:project-version:create')")
    fun createProjectVersion(
        @RequestParam("projectId") projectId: UUID,
        @RequestParam("versionName") versionName: String,
        @RequestParam("files") files: Array<MultipartFile>
    ): ResponseEntity<Any> {
        val requestDTO = ProjectVersionRequestDTO(projectId, versionName)
        val projectVersion = projectVersionService.createProjectVersion(requestDTO, files)
        return ResponseEntity.ok(projectVersion)
    }

    @PostMapping("/ci")
    fun createProjectVersionWithToken(
        @RequestParam("projectId") projectId: UUID,
        @RequestParam("versionName") versionName: String,
        @RequestParam("token") token: String,
        @RequestParam("files") files: Array<MultipartFile>
    ): ResponseEntity<Any> {
        val requestDTO = ProjectVersionRequestDTO(projectId, versionName)
        val projectVersion = projectVersionService.createProjectVersion(requestDTO, files, token)
        return ResponseEntity.ok(projectVersion)
    }

    @GetMapping
    fun getProjectVersions(
        @RequestParam("projectId", required = false) projectId: UUID?,
        @RequestParam("slug", required = false) slug: String?
    ): ResponseEntity<List<ProjectVersion>> {
        val canUserAccessProject = when {
            projectId != null -> projectService.canUserAccessProject(projectId)
            slug != null -> projectService.canUserAccessProject(slug)
            else -> false
        }

        if (!canUserAccessProject) {
            return ResponseEntity.notFound().build()
        }

        val projectVersions = when {
            projectId != null -> projectVersionService.getProjectVersions(projectId)
            slug != null -> projectVersionService.getProjectVersionsBySlug(slug)
            else -> return ResponseEntity.badRequest().build()
        }

        return ResponseEntity.ok(projectVersions)
    }

    @PatchMapping("/{versionId}")
    @PreAuthorize("hasAuthority('per:project-version:update')")
    fun updateDefaultVersion(
        @PathVariable versionId: UUID,
        @RequestBody request: UpdateDefaultVersionDTO
    ): ResponseEntity<Void> {
        projectVersionService.updateDefaultVersion(versionId, request.isDefault)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{versionId}")
    @PreAuthorize("hasAuthority('per:project-version:delete')")
    fun deleteProjectVersion(@PathVariable versionId: UUID): ResponseEntity<Void> {
        projectVersionService.deleteProjectVersion(versionId)
        return ResponseEntity.noContent().build()
    }
}
