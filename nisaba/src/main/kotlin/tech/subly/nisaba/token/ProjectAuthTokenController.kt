package tech.subly.nisaba.token


import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/auth-tokens")
class ProjectAuthTokenController(
    private val projectAuthTokenService: ProjectAuthTokenService
) {

    @PostMapping
    fun createToken(@RequestBody dto: ProjectAuthTokenRequestDTO): ResponseEntity<Map<String, String>> {
        val token = projectAuthTokenService.createToken(dto)
        return ResponseEntity.ok(mapOf("token" to token))
    }

    @DeleteMapping("/{id}")
    fun deleteToken(@PathVariable id: UUID): ResponseEntity<Any> {
        projectAuthTokenService.deleteToken(id)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/project/{projectId}")
    fun listTokensByProjectId(@PathVariable projectId: UUID): ResponseEntity<List<ProjectAuthToken>> {
        val tokens = projectAuthTokenService.listTokensByProjectId(projectId)
        return ResponseEntity.ok(tokens)
    }
}
