package tech.subly.nisaba.version


import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import tech.subly.nisaba.UserProvider
import tech.subly.nisaba.doc.ProjectVersionDoc
import tech.subly.nisaba.doc.ProjectVersionDocRepository
import tech.subly.nisaba.project.Project
import tech.subly.nisaba.project.ProjectRepository
import tech.subly.nisaba.token.ProjectAuthTokenService
import java.util.*

@Service
class ProjectVersionService(
    private val projectRepository: ProjectRepository,
    private val projectVersionRepository: ProjectVersionRepository,
    private val projectVersionDocRepository: ProjectVersionDocRepository,
    private val authTokenService: ProjectAuthTokenService,
    private val userProvider: UserProvider
) {
    @Transactional
    fun createProjectVersion(request: ProjectVersionRequestDTO, files: Array<MultipartFile>): ProjectVersion {
        val project =
            projectRepository.findById(request.projectId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found") }

        val isThereAnyVersionBefore = projectVersionRepository.existsByProject(project)
        // Retrieve or create the project version
        val projectVersion = projectVersionRepository.findByProjectAndVersionName(project, request.versionName)
            .orElseGet {
                projectVersionRepository.save(
                    ProjectVersion(
                        project = project,
                        versionName = request.versionName,
                        isDefault = !isThereAnyVersionBefore
                    )
                )
            }

        // Delete existing docs
        projectVersionDocRepository.deleteByProjectVersion(projectVersion)

        // Save new docs
        files.forEach { file ->
            val doc = ProjectVersionDoc(
                projectVersion = projectVersion, fileName = file.originalFilename
                    ?: "unknown", yamlContent = String(file.bytes)
            )
            projectVersionDocRepository.save(doc)
        }

        return projectVersion
    }

    @Transactional
    fun createProjectVersion(
        request: ProjectVersionRequestDTO,
        files: Array<MultipartFile>,
        token: String
    ): ProjectVersion {
        val authToken = authTokenService.validateTokenAndUpdateLastUsedDate(token, request.projectId)
        authToken ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "No token found for the default version"
        )
        return this.createProjectVersion(request, files)
    }

    fun getProjectVersions(projectId: UUID): List<ProjectVersion> {
        val project = projectRepository.findById(projectId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found") }
        return getProjectVersions(project)
    }

    fun getProjectVersionsBySlug(slug: String): List<ProjectVersion> {
        val project = projectRepository.findBySlug(slug)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found") }
        return getProjectVersions(project)
    }

    fun getProjectVersions(project: Project): List<ProjectVersion> {
        return userProvider.getUserDetails()
            .map { projectVersionRepository.findByProject(project) }
            .orElseGet {
                val projectVersion = projectVersionRepository.findFirstByProjectAndIsDefaultTrue(project)
                    .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Project version not found") }
                listOf(projectVersion)
            }
    }

    @Transactional
    fun updateDefaultVersion(versionId: UUID, isDefault: Boolean) {
        val version = projectVersionRepository.findById(versionId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Project version not found") }

        // If the update would change the default
        if (version.isDefault != isDefault) {
            // Clear default from project's other versions, if needed
            if (isDefault) {
                projectVersionRepository.clearDefaultFlag(version.project)
            }
            // Update the flag
            version.isDefault = isDefault
            projectVersionRepository.save(version)
        }
    }

    @Transactional
    fun deleteProjectVersion(versionId: UUID) {
        val version = projectVersionRepository.findById(versionId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Project version not found") }

        // Additional Logic (Optional):
        // - Delete associated files (if applicable)
        if (version.isDefault) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Cannot delete the default version"
            )
        }
        projectVersionDocRepository.deleteByProjectVersion(version)
        projectVersionRepository.delete(version)
    }
}
