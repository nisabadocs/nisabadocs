package tech.subly.nisaba.project

import jakarta.validation.constraints.NotBlank

data class ProjectRequestDTO(
        @field:NotBlank(message = "Project name cannot be blank")
        val name: String,
        val slug: String,
        var visibility: ProjectVisibility = ProjectVisibility.Internal
)
