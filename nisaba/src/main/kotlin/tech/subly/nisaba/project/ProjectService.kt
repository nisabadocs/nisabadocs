package tech.subly.nisaba.project

import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import tech.subly.nisaba.UserProvider
import tech.subly.nisaba.doc.ProjectVersionDocRepository
import tech.subly.nisaba.keycloak.KeycloakClientService
import tech.subly.nisaba.token.ProjectAuthTokenRepository
import tech.subly.nisaba.version.ProjectVersionRepository
import java.util.*
import java.util.regex.Pattern


@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val projectVersionRepository: ProjectVersionRepository,
    private val projectVersionDocRepository: ProjectVersionDocRepository,
    private val authTokenRepository: ProjectAuthTokenRepository,
    private val projectMemberRepository: ProjectMemberRepository,
    private val projectTeamRepository: ProjectTeamRepository,
    private val projectMemberService: ProjectMemberService,
    private val projectTeamService: ProjectTeamService,
    private val userProvider: UserProvider,
    private val keycloakClientService: KeycloakClientService
) {

    private val NON_ALPHANUMERIC = Pattern.compile("[^a-zA-Z0-9]")
    private val SLUG_PATTERN_REGEX: Pattern = Pattern.compile("^[a-zA-Z0-9]+(-[a-zA-Z0-9]+)*$")
    private val USER_CAN_ACCESS_ALL_PROJECT = "per:project:access:all"
    private val USER_CAN_ACCESS_INTERNAL_PROJECT = "per:project:access:internal"

    @Transactional
    fun createProject(projectRequestDTO: ProjectCreationRequestDTO): Project {
        val slug = generateUniqueSlug(projectRequestDTO.name)
        var project = Project(name = projectRequestDTO.name, slug = slug, visibility = ProjectVisibility.Internal)
        project = projectRepository.save(project)
        val userDetails = userProvider.getUserDetailsOrThrow()
        assignMembersToProject(project.id, listOf(userDetails.id))
        return project
    }

    fun generateUniqueSlug(projectName: String): String {
        val slugifiedName = slugify(projectName)
        var slug = slugifiedName
        var suffix = 1

        while (projectRepository.existsBySlug(slug)) {
            slug = "$slugifiedName-$suffix"
            suffix++
        }
        return slug
    }

    fun slugify(name: String): String {
        return NON_ALPHANUMERIC.matcher(name.trim().lowercase())
            .replaceAll("-")
    }

    @Transactional
    fun assignTeamsToProject(projectId: UUID, teamIds: List<String>) {
        val project = projectRepository.findById(projectId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found") }
        projectTeamService.assignTeamsToProject(project, teamIds)
    }

    @Transactional
    fun assignMembersToProject(projectId: UUID, userIds: List<String>) {
        val project = projectRepository.findById(projectId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found") }
        projectMemberService.assignMembersToProject(project, userIds)
    }

    @Transactional
    fun deleteProject(projectId: UUID) {
        val project = projectRepository.findById(projectId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found") }
        authTokenRepository.deleteByProjectId(projectId)
        projectMemberRepository.deleteByProjectId(projectId)
        projectTeamRepository.deleteByProjectId(projectId)
        projectVersionDocRepository.deleteByProjectId(projectId)
        projectVersionRepository.deleteByProjectId(projectId)
        projectRepository.delete(project)
    }

    fun findPublicProjects(): List<Project> {
        return projectRepository.findPublicProjects()
    }

    fun findAllProjects(): List<Project> {
        val userDetails = userProvider.getUserDetailsOrThrow()
        val projectVisibilities = getProjectVisibilitiesByUser(userDetails)
        val teamIds = keycloakClientService.getUserGroups(userDetails.id).map { it.id }
        return projectRepository.findProjectsByVisibilityOrUserMembership(userDetails.id, teamIds, projectVisibilities)
    }

    fun getProjectById(projectId: UUID): Project? {
        val userDetails = userProvider.getUserDetailsOrThrow()
        return projectRepository.findProjectsByVisibilityOrUserMembershipAndProjectId(
            userDetails.id,
            keycloakClientService.getUserGroups(userDetails.id).map { it.id },
            getProjectVisibilitiesByUser(userDetails),
            projectId
        ).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found") }
    }

    @Transactional
    fun updateProject(projectId: UUID, projectRequestDTO: ProjectRequestDTO): Project {
        if (!SLUG_PATTERN_REGEX.matcher(projectRequestDTO.slug).matches()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Slug format is invalid")
        }

        val existingProjectWithSlug = projectRepository.findBySlug(projectRequestDTO.slug)
        existingProjectWithSlug.ifPresent { project ->
            if (project.id != projectId) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Slug is already taken")
            }
        }

        val project = projectRepository.findById(projectId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found") }
        project.name = projectRequestDTO.name
        project.slug = projectRequestDTO.slug
        project.visibility = projectRequestDTO.visibility
        return projectRepository.save(project)
    }

    fun canUserAccessProject(slug: String): Boolean {
        return projectRepository.findBySlug(slug)
            .map { project -> canUserAccessProject(project.id) }
            .orElse(false)
    }

    fun canUserAccessProject(projectId: UUID): Boolean {
        val userDetailsOpt = userProvider.getUserDetails()
        if (userDetailsOpt.isEmpty) {
            return projectRepository.findById(projectId)
                .filter(Project::isPublic)
                .map { return@map true }
                .orElse(false)
        }
        val userDetails = userDetailsOpt.get()
        val projectVisibilities = getProjectVisibilitiesByUser(userDetails)
        val teamIds = keycloakClientService.getUserGroups(userDetails.id).map { it.id }
        return projectRepository.findProjectsByVisibilityOrUserMembershipAndProjectId(
            userDetails.id,
            teamIds,
            projectVisibilities,
            projectId
        ).isPresent
    }

    private fun getProjectVisibilitiesByUser(userDetails: UserProvider.CustomUserDetails): MutableList<ProjectVisibility> {
        if (userDetails.roles.contains(USER_CAN_ACCESS_ALL_PROJECT)) {
            return mutableListOf(ProjectVisibility.Public, ProjectVisibility.Internal, ProjectVisibility.Private)
        }
        if (userDetails.roles.contains(USER_CAN_ACCESS_INTERNAL_PROJECT)) {
            return mutableListOf(ProjectVisibility.Public, ProjectVisibility.Internal)
        }
        return mutableListOf(ProjectVisibility.Public)
    }
}
