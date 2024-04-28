package tech.subly.nisaba.doc

import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.server.ResponseStatusException
import tech.subly.nisaba.project.ProjectRepository
import tech.subly.nisaba.version.ProjectVersionRepository

@Service
class ProjectVersionDocService(
    private val projectVersionDocRepository: ProjectVersionDocRepository,
    private val projectVersionRepository: ProjectVersionRepository,
    private val projectRepository: ProjectRepository
) {

    @Transactional
    fun getProjectVersionDoc(projectDocQueryDTO: ProjectDocQueryDTO): List<ProjectVersionDoc> {
        val project = projectRepository.findBySlug(projectDocQueryDTO.slug)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found") }

        val projectVersion = if (StringUtils.hasText(projectDocQueryDTO.versionName)) {
            projectVersionRepository.findByProjectAndVersionName(project, projectDocQueryDTO.versionName!!)
        } else {
            projectVersionRepository.findFirstByProjectAndIsDefaultTrue(project)
        }.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Project version not found") }

        val docs = projectVersionDocRepository.findByProjectVersion(projectVersion)

        if (docs.isEmpty()) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "No documents found for the default version")
        }
        return docs
    }

}