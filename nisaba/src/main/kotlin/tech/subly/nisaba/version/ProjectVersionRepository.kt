package tech.subly.nisaba.version

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import tech.subly.nisaba.project.Project
import java.util.*


@Repository
interface ProjectVersionRepository : JpaRepository<ProjectVersion, UUID> {
    fun findByProjectAndVersionName(project: Project, versionName: String): Optional<ProjectVersion>
    fun findByProject(project: Project): List<ProjectVersion>
    fun existsByProject(project: Project): Boolean
    fun findFirstByProjectAndIsDefaultTrue(project: Project): Optional<ProjectVersion>

    @Modifying
    @Query("UPDATE ProjectVersion pv SET pv.isDefault = false WHERE pv.project = :project")
    fun clearDefaultFlag(@Param("project") project: Project?)

    @Modifying
    @Query("DELETE FROM ProjectVersion pv WHERE pv.project.id = :projectId")
    fun deleteByProjectId(@Param("projectId") projectId: UUID)
}
