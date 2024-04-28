package tech.subly.nisaba.project

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.util.*

@Entity
data class ProjectTeam(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "project_id")
    val project: Project,

    val teamId: String
) {
}