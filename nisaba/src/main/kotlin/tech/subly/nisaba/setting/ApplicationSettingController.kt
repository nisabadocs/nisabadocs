package tech.subly.nisaba.setting

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/settings")
class ApplicationSettingController(
    private val service: ApplicationSettingService
) {

    @GetMapping
    @PreAuthorize("hasAuthority('per:settings:view')")
    fun getSetting(): ResponseEntity<List<ApplicationSetting>> {
        return ResponseEntity.ok(service.fetchAllSettings())
    }

    @PostMapping
    @PreAuthorize("hasAuthority('per:settings:update')")
    fun saveSettings(@RequestBody settings: List<ApplicationSetting>): ResponseEntity<Void> {
        service.saveSetting(settings)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/logo")
    fun fetchLogo(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(mapOf("logo" to service.fetchLogo()))
    }

    @GetMapping("/viewer")
    fun fetchViewer(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(mapOf("viewer" to service.fetchViewer()))
    }

    @GetMapping("/landing-page")
    fun fetchLandingPage(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(mapOf("landingPage" to service.fetchLandingPage()))
    }

}
