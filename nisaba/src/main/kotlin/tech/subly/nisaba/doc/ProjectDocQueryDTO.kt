package tech.subly.nisaba.doc

import jakarta.validation.constraints.NotBlank

data class ProjectDocQueryDTO(
    @field:NotBlank(message = "Project slug cannot be blank")
    val slug: String,
    var versionName: String? = null
)