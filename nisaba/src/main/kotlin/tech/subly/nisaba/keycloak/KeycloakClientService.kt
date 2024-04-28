package tech.subly.nisaba.keycloak

import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response
import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.admin.client.resource.RolesResource
import org.keycloak.admin.client.resource.UsersResource
import org.keycloak.representations.idm.GroupRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import tech.subly.nisaba.KeycloakClientProperties
import tech.subly.nisaba.member.MemberDTO


@Service
class KeycloakClientService(
    private val userRoleService: UserRoleService,
    private val keycloak: Keycloak,
    private val keycloakClientProperties: KeycloakClientProperties
) {
    private val logger = LoggerFactory.getLogger(KeycloakClientService::class.java)
    private val DEFAULT_ROLE_NAME = "ROLE_VIEWER"
    private val MEMBER_ACTIONS_AFTER_INVITE = listOf("UPDATE_PASSWORD", "VERIFY_EMAIL", "UPDATE_PROFILE")

    private fun getRealmResource(): RealmResource {
        return keycloak.realm(keycloakClientProperties.realm)
    }

    fun getGroups(): List<GroupRepresentation> {
        return getRealmResource().groups().groups()
    }

    fun getGroupById(teamId: String): GroupRepresentation? {
        return getRealmResource().groups().group(teamId).toRepresentation()
    }

    fun createGroup(group: GroupRepresentation?): GroupRepresentation {
        if (group == null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Group representation must not be null")
        }

        val groupResources = getGroups().filter { it.name?.contains(group.name) ?: false }
        if (groupResources.isNotEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Team with name ${group.name} already exists")
        }

        val groupToCreate = GroupRepresentation().apply {
            name = group.name
        }

        val response: Response = getRealmResource().groups().add(groupToCreate)
        if (response.status == HttpStatus.CREATED.value()) {
            val groupId = CreatedResponseUtil.getCreatedId(response)
            val createdGroup = getRealmResource().groups().group(groupId).toRepresentation()
            response.close() // Close the response to release resources
            return createdGroup
        } else {
            val statusInfo = response.statusInfo
            val responseBody = response.readEntity(String::class.java)
            logger.error("Response Status: ${statusInfo.statusCode} (${statusInfo.reasonPhrase})")
            logger.error("Response Body: $responseBody")
            response.close()
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Team could not be created")
        }
    }

    fun getAllMembers(): List<UserRepresentation> {
        return getRealmResource().users().list()
    }

    fun getUsersWithRealmRoles(): List<MemberDTO> {
        val allMembers = getAllMembers()
        val userIds = allMembers.mapNotNull { it.id }
        val roles = userRoleService.findUserRoles(userIds)
            .filter { it.name.startsWith("ROLE_") }

        val rolesMap = roles.groupBy { it.userId }
            .mapValues { entry ->
                entry.value.filter { it.name.startsWith("ROLE_") }
                    .map { it.description ?: "" }
            }

        return allMembers.map { userRep ->
            val userId = userRep.id ?: throw IllegalStateException("User ID missing")
            val userRoles = rolesMap[userId] ?: listOf()
            val role = userRoles.firstOrNull() ?: ""

            MemberDTO(
                id = userId,
                username = userRep.username ?: "",
                firstName = userRep.firstName ?: "",
                lastName = userRep.lastName ?: "",
                email = userRep.email ?: "",
                role = role
            )
        }
    }


    fun getMembersByIds(userIds: List<String>): List<UserRepresentation> {
        val usersResource: UsersResource = getRealmResource().users()
        val keycloakUsers = usersResource.list().filter { userRep ->
            userIds.contains(userRep.id)
        }
        return keycloakUsers
    }

    fun getTeamsByIds(teamIds: List<String>): List<GroupRepresentation> {
        val groups = getRealmResource().groups().groups()
        val teams = groups.filter { groupRep ->
            teamIds.contains(groupRep.id)
        }
        return teams
    }

    fun createMember(user: UserRepresentation): UserRepresentation {
        if (emailExists(user.email)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User with email ${user.email} already exists")
        }

        val newUser = user.apply { isEnabled = true }
        val usersResource: UsersResource = getRealmResource().users()
        val response: Response = usersResource.create(newUser)

        if (response.status == HttpStatus.CREATED.value()) {
            val userId = CreatedResponseUtil.getCreatedId(response)
            val userResource = usersResource.get(userId)
            userResource.executeActionsEmail(MEMBER_ACTIONS_AFTER_INVITE)
            assignRealmRoleToUser(userId, DEFAULT_ROLE_NAME)
            return userResource.toRepresentation()
        } else {
            // Log or handle the error appropriately
            val statusInfo = response.statusInfo
            val responseBody = response.readEntity(String::class.java)
            logger.error("Response Status: ${statusInfo.statusCode} (${statusInfo.reasonPhrase})")
            logger.error("Response Body: $responseBody")
            response.close()
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User could not be created")
        }
    }

    private fun emailExists(email: String): Boolean {
        return getAllMembers()
            .any { it.email?.contains(email, ignoreCase = true) ?: false }
    }

    fun addUserToGroup(teamId: String, userId: String) {
        val userResource = getRealmResource().users().get(userId)

        userResource.joinGroup(teamId) // Add the user to the group
    }

    fun removeUserFromGroup(teamId: String, userId: String) {
        val userResource = getRealmResource().users().get(userId)
        userResource.leaveGroup(teamId) // Remove the user from the group
    }

    fun getGroupMembers(teamId: String): List<UserRepresentation> {
        val groupResource = getRealmResource().groups().group(teamId)
        return groupResource.members()
    }


    fun deleteUserById(userId: String) {
        val realm = getRealmResource()
        val usersResource = realm.users()

        try {
            usersResource.delete(userId)
        } catch (e: WebApplicationException) {
            logger.error("Could not delete user", e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not delete user")
        }
    }

    fun deleteGroupById(groupId: String) {
        val realm = getRealmResource()
        val groupsResource = realm.groups()

        try {
            groupsResource.group(groupId).remove()
        } catch (e: WebApplicationException) {
            logger.error("Could not delete group", e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not delete group")
        }
    }

    fun assignRealmRoleToUser(userId: String, roleName: String?) {
        val usersResource = getRealmResource().users()
        val userResource = usersResource.get(userId) ?: throw NotFoundException("User not found")

        // Find the role representation
        val roleRepresentation = getRealmResource()
            .roles()
            .get(roleName)
            .toRepresentation()

        // first add new role
        userResource.roles().realmLevel().add(listOf(roleRepresentation))
        // remove all roles
        userResource.roles().realmLevel().remove(userResource.roles().realmLevel().listAll()
            .filter { !it.name.equals(roleName) && it.name.startsWith("ROLE_") })

    }

    fun listFilteredRealmRoles(): List<RoleRepresentation> {
        val rolesResource: RolesResource = getRealmResource().roles()
        return rolesResource.list()
            .filter { it.name.startsWith("ROLE_") }
    }

    fun getUserGroups(userId: String): List<GroupRepresentation> {
        val userResource = getRealmResource().users().get(userId)
        return try {
            userResource.groups()
        } catch (e: Exception) {
            logger.error("Could not fetch groups for user", e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not fetch groups for user")
        }
    }

}
