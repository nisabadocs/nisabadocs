package tech.subly.nisaba.setting

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import java.util.*

@Entity
@Table(name = "settings")
data class ApplicationSetting(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    var id: UUID = UUID.randomUUID(),

    @Column(name = "setting_key", nullable = false, unique = true)
    val settingKey: String,

    @Column(name = "setting_value", nullable = false)
    var settingValue: String,

    @Column(name = "setting_type", nullable = false)
    val settingType: String,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}
