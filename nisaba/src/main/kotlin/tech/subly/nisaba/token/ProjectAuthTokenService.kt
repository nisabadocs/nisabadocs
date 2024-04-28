package tech.subly.nisaba.token

import org.apache.commons.text.RandomStringGenerator
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import tech.subly.nisaba.project.ProjectRepository
import java.time.Instant
import java.util.*

@Service
class ProjectAuthTokenService(
    private val passwordEncoder: PasswordEncoder,
    private val projectRepository: ProjectRepository,
    private val projectAuthTokenRepository: ProjectAuthTokenRepository
) {

    fun createToken(dto: ProjectAuthTokenRequestDTO): String {
        val project = projectRepository.findById(UUID.fromString(dto.projectId))
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found") }
        val rawToken = generateRandomString()
        val encodedToken = passwordEncoder.encode(rawToken)
        val token = ProjectAuthToken(
            project = project,
            name = dto.name,
            authToken = encodedToken
        )
        projectAuthTokenRepository.save(token)
        return rawToken
    }

    fun deleteToken(id: UUID) {
        if (!projectAuthTokenRepository.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Token not found")
        }
        projectAuthTokenRepository.deleteById(id)
    }

    fun listTokensByProjectId(projectId: UUID): List<ProjectAuthToken> {
        return projectAuthTokenRepository.findByProjectId(projectId)
    }

    fun validateTokenAndUpdateLastUsedDate(rawToken: String, projectId: UUID): ProjectAuthToken? {
        val projectTokens = projectAuthTokenRepository.findByProjectId(projectId)
        val matchedToken = projectTokens.find { passwordEncoder.matches(rawToken, it.authToken) }
        matchedToken?.let {
            it.lastUsedDate = Instant.now()
            projectAuthTokenRepository.save(it)
        }
        return matchedToken
    }

    fun generateRandomString(length: Int = 32): String {
        val generator = RandomStringGenerator.Builder()
            .withinRange('0'.code, 'z'.code)
            .filteredBy(Character::isLetterOrDigit)
            .build()
        return generator.generate(length)
    }

}
