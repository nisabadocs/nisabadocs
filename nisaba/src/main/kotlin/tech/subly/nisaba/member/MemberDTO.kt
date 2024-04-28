package tech.subly.nisaba.member

data class MemberDTO (
    val id: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String
)