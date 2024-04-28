package tech.subly.nisaba.token

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import tech.subly.nisaba.serializer.InstantSerializer
import tech.subly.nisaba.project.Project
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "project_auth_token")
data class ProjectAuthToken(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    val project: Project,
    val name: String,
    @JsonIgnore
    val authToken: String,
    @JsonSerialize(using = InstantSerializer::class)
    val creationDate: Instant = Instant.now(),
    @JsonSerialize(using = InstantSerializer::class)
    var lastUpdateDate: Instant = Instant.now(),
    @JsonSerialize(using = InstantSerializer::class)
    var lastUsedDate: Instant? = null
) {
    @PreUpdate
    fun onUpdate() {
        lastUpdateDate = Instant.now()
    }
}
