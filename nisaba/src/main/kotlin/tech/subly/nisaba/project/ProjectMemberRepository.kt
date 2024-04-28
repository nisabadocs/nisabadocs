package tech.subly.nisaba.project

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*


interface ProjectMemberRepository : JpaRepository<ProjectMember, Long> {
    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id = :projectId")
    fun findProjectMembersByProjectId(@Param("projectId") projectId: UUID): Set<ProjectMember>

    @Modifying
    @Query("DELETE FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.memberId = :userId")
    fun deleteByProjectIdAndUserId(@Param("projectId") projectId: UUID?, @Param("userId") userId: String?)

    @Modifying
    @Query("DELETE FROM ProjectMember pm WHERE pm.memberId = :userId")
    fun deleteByUserId(@Param("userId") userId: String?)

    @Modifying
    @Query("DELETE FROM ProjectMember pm WHERE pm.project.id = :projectId")
    fun deleteByProjectId(@Param("projectId") projectId: UUID)

}