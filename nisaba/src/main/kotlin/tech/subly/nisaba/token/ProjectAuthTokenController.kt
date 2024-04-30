package tech.subly.nisaba.token


import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/auth-tokens")
class ProjectAuthTokenController(
    private val projectAuthTokenService: ProjectAuthTokenService
) {

    @PostMapping
    @PreAuthorize("hasAuthority('per:project-token:create')")
    fun createToken(@RequestBody dto: ProjectAuthTokenRequestDTO): ResponseEntity<Map<String, String>> {
        val token = projectAuthTokenService.createToken(dto)
        return ResponseEntity.ok(mapOf("token" to token))
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('per:project-token:delete')")
    fun deleteToken(@PathVariable id: UUID): ResponseEntity<Any> {
        projectAuthTokenService.deleteToken(id)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAuthority('per:project-token:view')")
    fun listTokensByProjectId(@PathVariable projectId: UUID): ResponseEntity<List<ProjectAuthToken>> {
        val tokens = projectAuthTokenService.listTokensByProjectId(projectId)
        return ResponseEntity.ok(tokens)
    }

}
