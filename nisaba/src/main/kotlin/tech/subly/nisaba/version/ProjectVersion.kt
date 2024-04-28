package tech.subly.nisaba.version

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import tech.subly.nisaba.serializer.InstantSerializer
import tech.subly.nisaba.project.Project
import java.time.Instant
import java.util.*

@Entity
@Table(name = "project_version")
data class ProjectVersion(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    val project: Project,

    val versionName: String,

    @JsonSerialize(using = InstantSerializer::class)
    val creationDate: Instant = Instant.now(),

    var isDefault: Boolean = false
)
