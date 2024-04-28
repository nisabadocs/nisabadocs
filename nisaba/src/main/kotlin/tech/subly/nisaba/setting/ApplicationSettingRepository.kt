package tech.subly.nisaba.setting

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ApplicationSettingRepository : JpaRepository<ApplicationSetting, Long> {
    fun findBySettingKey(settingKey: String): Optional<ApplicationSetting>
}
