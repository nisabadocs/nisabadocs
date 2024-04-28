package tech.subly.nisaba.version

import java.util.*

data class ProjectVersionRequestDTO(
        val projectId: UUID,
        val versionName: String
)
