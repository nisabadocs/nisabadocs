package tech.subly.nisaba.keycloak
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Service

@Service
class UserRoleService(@PersistenceContext private val entityManager: EntityManager) {

    fun findUserRoles(userIds: List<String>): List<UserRoleDto> {
        val sql = """
            SELECT urm.user_id, kr.description, kr.name
            FROM keycloak_role kr
            INNER JOIN user_role_mapping urm ON urm.role_id = kr.id
            WHERE urm.user_id = ANY(?)
        """.trimIndent()

        val query = entityManager.createNativeQuery(sql)
        query.setParameter(1, userIds.toTypedArray()) // Handling List for IN clause

        val result = query.resultList
        val dtos = mutableListOf<UserRoleDto>()

        for (obj in result) {
            val array = obj as Array<*>
            dtos.add(UserRoleDto(
                userId = array[0] as String,
                description = array[1] as String?,
                name = array[2] as String
            ))
        }

        return dtos.toList()
    }
}
