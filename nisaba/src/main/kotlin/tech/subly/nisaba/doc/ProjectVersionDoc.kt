package tech.subly.nisaba.doc

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import tech.subly.nisaba.serializer.InstantSerializer
import tech.subly.nisaba.version.ProjectVersion
import java.time.Instant
import java.util.*

@Entity
@Table(name = "project_version_doc")
data class ProjectVersionDoc(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_version_id")
    val projectVersion: ProjectVersion,

    val fileName: String,

    @Lob
    val yamlContent: String,
    @JsonSerialize(using = InstantSerializer::class)
    val creationDate: Instant = Instant.now()
)
