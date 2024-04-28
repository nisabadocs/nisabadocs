package tech.subly.nisaba.project

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import tech.subly.nisaba.serializer.InstantSerializer
import java.time.Instant
import java.util.*

@Entity
data class Project(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    var id: UUID = UUID.randomUUID(),
    var name: String = "",
    @Column(unique = true)
    var slug: String = "",
    val deleted: Boolean = false,
    @JsonSerialize(using = InstantSerializer::class)
    val creationDate: Instant = Instant.now(),
    @JsonSerialize(using = InstantSerializer::class)
    var lastUpdateDate: Instant = Instant.now(),
    @Enumerated(EnumType.STRING)
    var visibility: ProjectVisibility = ProjectVisibility.Internal
) {
    @PreUpdate
    fun onUpdate() {
        lastUpdateDate = Instant.now()
    }

    fun isPublic(): Boolean {
        return visibility == ProjectVisibility.Public
    }

}
