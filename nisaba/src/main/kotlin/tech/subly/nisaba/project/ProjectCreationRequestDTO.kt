package tech.subly.nisaba.project

import jakarta.validation.constraints.NotBlank

data class ProjectCreationRequestDTO(
        @field:NotBlank(message = "Project name cannot be blank")
        val name: String,
)
