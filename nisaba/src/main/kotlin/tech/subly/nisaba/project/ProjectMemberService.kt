package tech.subly.nisaba.project

import jakarta.transaction.Transactional
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.stereotype.Service
import tech.subly.nisaba.keycloak.KeycloakClientService
import java.util.*


@Service
class ProjectMemberService(
    private val keycloakClientService: KeycloakClientService,
    private val projectMemberRepository: ProjectMemberRepository
) {

    fun getProjectMembers(projectId: UUID): List<UserRepresentation> {
        val projectMembers = projectMemberRepository.findProjectMembersByProjectId(projectId)
        val members = projectMembers.map { it.memberId }.toList()
        return keycloakClientService.getMembersByIds(members)
    }

    @Transactional
    fun assignMembersToProject(project: Project, userIds: List<String>) {
        userIds.forEach { userId ->
            val projectMember = ProjectMember(project= project, memberId = userId)
            projectMemberRepository.save(projectMember)
        }
    }

    @Transactional
    fun removeMemberFromProject(projectId: UUID?, memberId: String?) {
        projectMemberRepository.deleteByProjectIdAndUserId(projectId, memberId)
    }

}
