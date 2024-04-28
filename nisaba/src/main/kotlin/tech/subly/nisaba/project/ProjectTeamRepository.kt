package tech.subly.nisaba.project

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*


interface ProjectTeamRepository : JpaRepository<ProjectTeam, Long> {
    @Query("SELECT pt FROM ProjectTeam pt WHERE pt.project.id = :projectId")
    fun findProjectTeamsByProjectId(@Param("projectId") projectId: UUID): Set<ProjectTeam>

    @Modifying
    @Query("DELETE FROM ProjectTeam pt WHERE pt.project.id = :projectId AND pt.teamId = :teamId")
    fun deleteByProjectIdAndTeamId(@Param("projectId") projectId: UUID?, @Param("teamId") teamId: String?)

    @Modifying
    @Query("DELETE FROM ProjectTeam pt WHERE pt.teamId = :teamId")
    fun deleteByTeamId(@Param("teamId") teamId: String?)

    @Modifying
    @Query("DELETE FROM ProjectTeam pt WHERE pt.project.id = :projectId")
    fun deleteByProjectId(@Param("projectId") projectId: UUID)
}