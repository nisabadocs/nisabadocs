package tech.subly.nisaba.setting

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ApplicationSettingService(
    private val repository: ApplicationSettingRepository
) {
    private val LOGO_SETTING_KEY = "logo"
    private val DEFAULT_VIEWER_KEY = "default-viewer"
    private val LANDING_PAGE_KEY = "landing-page"
    fun fetchAllSettings(): List<ApplicationSetting> {
        return repository.findAll()
    }

    fun saveSetting(settings: List<ApplicationSetting>): List<ApplicationSetting> {
        return repository.saveAll(settings)
    }

    fun fetchLogo(): String {
        return repository.findBySettingKey(LOGO_SETTING_KEY)
            .map { it.settingValue }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Settings not found") }
    }

    fun fetchViewer(): String {
        return repository.findBySettingKey(DEFAULT_VIEWER_KEY)
            .map { it.settingValue }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Settings not found") }
    }

    fun fetchLandingPage(): String {
        return repository.findBySettingKey(LANDING_PAGE_KEY)
            .map { it.settingValue }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Settings not found") }
    }

}
