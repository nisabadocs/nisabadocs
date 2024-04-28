package tech.subly.nisaba.keycloak

data class UserRoleDto(
    val userId: String,
    val description: String?,
    val name: String
)
