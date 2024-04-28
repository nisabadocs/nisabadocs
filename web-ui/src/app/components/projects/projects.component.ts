import {Component, OnInit} from '@angular/core';
import {ProjectService} from '../../services/project.service';
import {Project} from '../../models/project.model';
import {Router} from '@angular/router';
import {MenuItem} from "primeng/api";
import {RoleCheckerService} from "../../services/role-checker.service";
import {KeycloakService} from "keycloak-angular";

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.scss']
})
export class ProjectsComponent implements OnInit {
  splitButtonItems: MenuItem[] = [
    {
      label: 'Open Swagger',
      icon: 'pi pi-code',
      command: () => {
        this.openDocumentation(this.clickedProject!, 'swagger');
      }
    },
    {
      label: 'Open Stoplight',
      icon: 'pi pi-eye',
      command: () => {
        this.openDocumentation(this.clickedProject!, 'stoplight');
      }
    },
    {
      label: 'Open Redoc',
      icon: 'pi pi-file',
      command: () => {
        this.openDocumentation(this.clickedProject!, 'redoc');
      }
    }
  ];
  projects: Project[] = [];
  displayDialog: boolean = false;
  project: Project = {id: '', name: '', slug: '', creationDate: new Date(), lastUpdateDate: new Date()};
  clickedProject: Project | null = null;

  constructor(private projectService: ProjectService,
              private router: Router,
              public roleChecker: RoleCheckerService,
              private keycloakService: KeycloakService
  ) {
  }

  ngOnInit(): void {
    this.fetchProjects();
  }

  fetchProjects() {
    if(this.keycloakService.isLoggedIn()){
      this.projectService.getProjects().subscribe((projects: Project[]) => {
        this.projects = projects;
      });
    } else {
      this.projectService.getPublicProjects().subscribe((projects: Project[]) => {
        this.projects = projects;
      });
    }
  }

  showDialog() {
    this.displayDialog = true;
  }

  createProject() {
    this.projectService.createProject(this.project).subscribe(() => {
      this.fetchProjects();
      this.displayDialog = false;
      this.project = {id: '', name: '', slug: '', creationDate: new Date(), lastUpdateDate: new Date()}; // Reset form
    });
  }

  openDocumentation(project: Project, type: string): void {
    if (type === 'default') {
      this.router.navigate(['docs', project.slug])
    } else {
      this.router.navigate(['viewer', type, 'docs', project.slug])
    }
  }

  navigateToProjectDetails(projectId: string) {
    this.router.navigate(['/projects', projectId]);
  }


  handleActionsClick(project: Project) {
    console.log('Actions clicked for project:', project);
    this.clickedProject = project;
  }
}
