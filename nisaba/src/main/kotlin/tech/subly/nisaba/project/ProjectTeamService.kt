package tech.subly.nisaba.project

import jakarta.transaction.Transactional
import org.keycloak.representations.idm.GroupRepresentation
import org.springframework.stereotype.Service
import tech.subly.nisaba.keycloak.KeycloakClientService
import java.util.*


@Service
class ProjectTeamService(
    private val keycloakClientService: KeycloakClientService,
    private val projectTeamRepository: ProjectTeamRepository
) {

    fun getProjectTeams(projectId: UUID): List<GroupRepresentation> {
        val projectMembers = projectTeamRepository.findProjectTeamsByProjectId(projectId)
        val teams = projectMembers.map { it.teamId }.toList()
        return keycloakClientService.getTeamsByIds(teams)
    }

    @Transactional
    fun assignTeamsToProject(project: Project, teamIds: List<String>) {
        teamIds.forEach { teamId ->
            projectTeamRepository.save(ProjectTeam(project = project, teamId = teamId))
        }
    }

    @Transactional
    fun removeTeamFromProject(projectId: UUID?, teamId: String?) {
        projectTeamRepository.deleteByProjectIdAndTeamId(projectId, teamId)
    }
}
