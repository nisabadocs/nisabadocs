package tech.subly.nisaba.project

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProjectRepository : JpaRepository<Project, UUID> {

    fun findBySlug(slug: String): Optional<Project>

    fun existsBySlug(slug: String): Boolean

    @Query("""
        SELECT DISTINCT p FROM Project p 
        LEFT JOIN ProjectMember pm ON p.id = pm.project.id
        LEFT JOIN ProjectTeam pt ON p.id = pt.project.id
        WHERE p.visibility IN :visibilities
        OR pm.memberId = :memberId
        OR pt.teamId IN :teamIds
        order by p.name asc
    """)
    fun findProjectsByVisibilityOrUserMembership(
        @Param("memberId") memberId: String,
        @Param("teamIds") teamIds: List<String>,
        @Param("visibilities") visibilities: List<ProjectVisibility>
    ): List<Project>

    @Query("""
        SELECT DISTINCT p FROM Project p 
        LEFT JOIN ProjectMember pm ON p.id = pm.project.id
        LEFT JOIN ProjectTeam pt ON p.id = pt.project.id
        WHERE (p.visibility IN :visibilities
        OR pm.memberId = :memberId
        OR pt.teamId IN :teamIds) 
        AND p.id = :projectId
        order by p.name asc
    """)
    fun findProjectsByVisibilityOrUserMembershipAndProjectId(
        @Param("memberId") memberId: String,
        @Param("teamIds") teamIds: List<String>,
        @Param("visibilities") visibilities: List<ProjectVisibility>,
        @Param("projectId") projectId: UUID
    ): Optional<Project>

    @Query("""
        SELECT DISTINCT p FROM Project p 
        WHERE p.visibility = tech.subly.nisaba.project.ProjectVisibility.Public
        order by p.name asc
    """)
    fun findPublicProjects(): List<Project>

}
