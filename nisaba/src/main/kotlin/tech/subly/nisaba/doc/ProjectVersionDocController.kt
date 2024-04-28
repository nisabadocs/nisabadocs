package tech.subly.nisaba.doc

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tech.subly.nisaba.project.ProjectService

@RestController()
@RequestMapping("/api/project-docs")
class ProjectVersionDocController(
    private val projectVersionDocService: ProjectVersionDocService,
    private val projectService: ProjectService
) {

    @PostMapping
    fun getProjectDocs(@RequestBody projectDocQueryDTO: ProjectDocQueryDTO): ResponseEntity<List<ProjectVersionDoc>> {
        val canUserAccessProject = projectService.canUserAccessProject(projectDocQueryDTO.slug)
        if (!canUserAccessProject) {
            return ResponseEntity.notFound().build()
        }
        val docContent =
            projectVersionDocService.getProjectVersionDoc(projectDocQueryDTO)
        return ResponseEntity.ok()
            .body(docContent)
    }

}