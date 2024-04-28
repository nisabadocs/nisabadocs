package tech.subly.nisaba.token

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ProjectAuthTokenRepository : JpaRepository<ProjectAuthToken, UUID> {
    fun findByProjectId(projectId: UUID): List<ProjectAuthToken>
    @Modifying
    @Query("DELETE FROM ProjectAuthToken auth WHERE auth.project.id = :projectId")
    fun deleteByProjectId(@Param("projectId") projectId: UUID)
}
