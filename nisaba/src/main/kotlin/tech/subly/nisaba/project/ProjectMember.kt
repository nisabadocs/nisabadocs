package tech.subly.nisaba.project

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.util.*

@Entity
class ProjectMember(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    var id: UUID? = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "project_id")
    val project: Project,

    val memberId: String
)