package tech.subly.nisaba.doc

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import tech.subly.nisaba.version.ProjectVersion
import java.util.*

@Repository
interface ProjectVersionDocRepository : JpaRepository<ProjectVersionDoc, UUID> {
    fun deleteByProjectVersion(projectVersion: ProjectVersion)

    fun findByProjectVersion(projectVersion: ProjectVersion): List<ProjectVersionDoc>

    @Modifying
    @Query("DELETE FROM ProjectVersionDoc pvd WHERE pvd.projectVersion.project.id = :projectId")
    fun deleteByProjectId(@Param("projectId") projectId: UUID)
}