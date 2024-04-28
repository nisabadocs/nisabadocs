package tech.subly.nisaba.token

import jakarta.validation.constraints.NotBlank

data class ProjectAuthTokenRequestDTO (
    @field:NotBlank(message = "Project name cannot be blank")
    val name: String,
    @field:NotBlank(message = "Project ID cannot be blank")
    val projectId: String
)
