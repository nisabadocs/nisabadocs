import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ProjectService} from '../../services/project.service';
import {Team} from "../../models/team.model";
import {Member} from "../../models/member.model";
import {forkJoin} from "rxjs";
import {MembersService} from "../../services/members.service";
import {TeamService} from "../../services/teams.service";
import {ProjectVersionsService} from "../../services/project-versions.service";
import {ProjectVersion} from "../../models/project-version.model";
import {DropdownChangeEvent} from "primeng/dropdown";
import {ProjectAuthService} from "../../services/project-auth.service";
import {ConfirmationService, MenuItem} from "primeng/api";
import {Location} from '@angular/common';
import {environment} from "../../../environments/environment";
import {Project} from "../../models/project.model";
import {RoleCheckerService} from "../../services/role-checker.service";
import {ProjectAuthToken} from "../../models/project-auth-token.model";


@Component({
  selector: 'app-project-details',
  templateUrl: './project-details.component.html',
  styleUrls: ['./project-details.component.scss']
})
export class ProjectDetailsComponent implements OnInit {
  project: Project | null = null;
  teams: Team[] = [];
  members: Member[] = [];
  availableTeams: Team[] = [];
  availableMembers: Member[] = [];
  projectVersions: ProjectVersion[] = [];
  selectedVersion: ProjectVersion | null = null;
  token?: string = '';
  baseUrl = environment.apiUrl;
  isDirty: boolean = false;
  selectedTeams: Team[] = [];
  selectedMembers: Member[] = [];
  projectTokens: ProjectAuthToken[] = [];
  visibilityOptions = [
    {label: 'Public', value: 'Public'},
    {label: 'Internal', value: 'Internal'},
    {label: 'Private', value: 'Private'}
  ];
  clickedVersion: ProjectVersion | null = null;
  splitButtonItems: MenuItem[] = [
    {
      label: 'Open Swagger',
      icon: 'pi pi-code',
      command: () => {
        this.openDocumentation(this.clickedVersion!, 'swagger');
      }
    },
    {
      label: 'Open Stoplight',
      icon: 'pi pi-eye',
      command: () => {
        this.openDocumentation(this.clickedVersion!, 'stoplight');
      }
    },
    {
      label: 'Open Redoc',
      icon: 'pi pi-file',
      command: () => {
        this.openDocumentation(this.clickedVersion!, 'redoc');
      }
    },
    {
      label: "Delete Version",
      icon: "pi pi-trash",
      visible: this.roleChecker.hasProjectVersionDeleteRole(),
      command: () => {
        this.deleteVersion(this.clickedVersion!);
      }
    }
  ];
  displayAuthTokenDialog: boolean = false;
  authToken: ProjectAuthToken = {name: ''};

  constructor(private route: ActivatedRoute,
              private projectService: ProjectService,
              private teamService: TeamService,
              private memberService: MembersService,
              private projectVersionsService: ProjectVersionsService,
              private authService: ProjectAuthService,
              private confirmationService: ConfirmationService,
              private router: Router,
              private location: Location,
              public roleChecker: RoleCheckerService) {
  }

  ngOnInit(): void {
    this.extracted();
  }

  private extracted() {
    const projectId = this.route.snapshot.paramMap.get('projectId');
    if (!projectId) {
      this.router.navigate(['/404']);
      return;
    }
    this.projectService.getProjectDetails(projectId).subscribe(project => {
      this.project = project;
      this.loadTeams(projectId);
      this.loadMembers(projectId);
      this.loadProjectTokens();
      this.loadVersions(projectId);
    }, _ => {
      this.router.navigate(['/404']);
    });

  }

  private loadVersions(projectId: string) {
    this.projectVersionsService.getProjectVersions(projectId).subscribe(versions => {
      this.projectVersions = versions;
      this.selectedVersion = versions.find(v => v.isDefault) || null;
    });
  }

  private loadMembers(projectId: string) {
    if (this.roleChecker.hasProjectMemberViewRole()) {
      forkJoin([
        this.projectService.getProjectMembers(projectId),
        this.memberService.getMembers()
      ]).subscribe({
        next: ([members, allMembers]) => {
          this.members = members as Member[];
          allMembers = allMembers.filter(member => member.email);
          this.availableMembers = allMembers.filter(member => !this.members.some(m => m.id === member.id));
        }
      });
    }
  }

  private loadTeams(projectId: string) {
    if (this.roleChecker.hasProjectTeamViewRole()) {
      forkJoin([
        this.projectService.getProjectTeams(projectId),
        this.teamService.getTeams()
      ]).subscribe({
        next: ([teams, allTeams]) => {
          this.teams = teams as Team[];
          this.availableTeams = allTeams.filter(team => !this.teams.some(t => t.id === team.id));
        }
      });
    }
  }

  addTeam() {
    const teamIds = this.selectedTeams
      .map(value => value.id) // Map to ids, resulting in (string | undefined)[]
      .filter((id): id is string => !!id); // Filter out undefined values, resulting in string[]

    this.projectService.assignTeamToProject(this.project?.id!, teamIds)
      .subscribe({
        next: () => {
          const teams = this.availableTeams.filter(team => teamIds.includes(team.id!));
          if (teams) {
            this.teams.push(...teams);
          }
          this.availableTeams = this.availableTeams.filter(team => !teamIds.includes(team.id!));
          this.selectedTeams = [];
        }
      });
  }

  removeTeam(team: Team) {
    this.confirmationService.confirm({
      message: `Are you sure you want to remove ${team.name} from the project?`,
      accept: () => {
        const teamId = team.id!;
        this.projectService.removeTeamFromProject(this.project?.id!, teamId)
          .subscribe({
            next: () => {
              const team = this.teams.find(t => t.id === teamId) as Team;
              this.teams = this.teams.filter(t => t.id !== teamId);
              this.availableTeams = [...this.availableTeams, team];
            }
          });
      }
    });

  }

  addMember() {
    const memberIds = this.selectedMembers
      .map(value => value.id) // Map to ids, resulting in (string | undefined)[]
      .filter((id): id is string => !!id); // Filter out undefined values, resulting in string[]
    this.projectService.assignMemberToProject(this.project?.id!, memberIds)
      .subscribe({
        next: () => {
          const members = this.availableMembers.filter(m => memberIds.includes(m.id!));
          if (members) {
            this.members.push(...members);
          }
          this.availableMembers = this.availableMembers.filter(m => !memberIds.includes(m.id!));
          this.selectedMembers = [];
        }
      });
  }

  removeMember(member: Member) {
    this.confirmationService.confirm({
      message: `Are you sure you want to remove ${member.email} from the project?`,
      accept: () => {
        const memberId = member.id;
        this.projectService.removeMemberFromProject(this.project?.id!, memberId!)
          .subscribe({
            next: () => {
              const member = this.members.find(m => m.id === memberId) as Member;
              this.members = this.members.filter(m => m.id !== memberId);
              this.availableMembers = [...this.availableMembers, member];
            }
          });
      }
    });

  }

  onVersionChange(event: DropdownChangeEvent) {
    const newDefaultVersion = event.value as ProjectVersion;
    this.projectVersionsService.updateDefaultVersion(newDefaultVersion.id)
      .subscribe({
        next: () => {
          // Update `isDefault` flags locally
          this.projectVersions.forEach(v => v.isDefault = v.id === newDefaultVersion.id);
          this.selectedVersion = newDefaultVersion; // Update dropdown selection
        }
      });
  }

  deleteVersion(version: ProjectVersion) {
    this.confirmationService.confirm({
      message: `Are you sure you want to delete ${version.versionName}?`,
      accept: () => {
        const versionId = version.id;
        this.projectVersionsService.deleteProjectVersion(versionId)
          .subscribe({
            next: () => {
              this.projectVersions = this.projectVersions.filter(v => v.id !== versionId);
              // Reset dropdown if the deleted version was the default
              if (this.selectedVersion && this.selectedVersion.id === versionId) {
                this.selectedVersion = this.projectVersions.find(v => v.isDefault) || null;
              }
            }
          });
      }
    });

  }

  openDocumentation(version: ProjectVersion, type: string): void {
    sessionStorage.setItem('previousUrl', this.location.path());
    if (type === 'default') {
      this.router.navigate(['docs', this.project!.slug, 'version', version.versionName])
    } else {
      this.router.navigate(['viewer', type, 'docs', this.project!.slug, 'version', version.versionName])
    }
  }

  updateProject() {
    if (!this.project) return;

    this.confirmationService.confirm({
      message: 'Are you sure you want to save the changes?',
      accept: () => {
        this.projectService.updateProject(this.project!.id, this.project!).subscribe(() => {
            this.isDirty = false;
            this.extracted();
          }
        );
      }
    });
  }

  handleActionsClick(version: ProjectVersion) {
    this.clickedVersion = version;
  }

  confirmDeleteProject(project: Project) {
    this.confirmationService.confirm({
      message: `Are you sure you want to delete ${project.name}?`,
      accept: () => {
        this.deleteProject(project.id);
      }
    });
  }

  deleteProject(projectId: string): void {
    this.projectService.deleteProject(projectId).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (error) => {
        console.error('Error deleting project:', error);
      }
    });
  }

  loadProjectTokens() {
    if (this.roleChecker.hasProjectTokenViewRole()) {
      this.authService.listTokensByProjectId(this.project?.id!).subscribe({
        next: (tokens) => this.projectTokens = tokens
      });
    }
  }

  confirmDeleteToken(token: ProjectAuthToken) {
    this.confirmationService.confirm({
      message: `Are you sure you want to delete ${token.name}?`,
      accept: () => {
        this.deleteToken(token.id!);
      }
    });
  }

  deleteToken(tokenId: string) {
    this.authService.deleteToken(tokenId).subscribe({
      next: () => this.loadProjectTokens()
    });
  }

  createAuthToken() {
    this.authService.createToken(this.project?.id!, this.authToken.name!).subscribe({
      next: (data: ProjectAuthToken) => {
        this.loadProjectTokens();
        this.displayAuthTokenDialog = false;
        this.token = data.token;
        this.authToken = {name: ''};
      }
    })
  }

  showAuthTokenDialog() {
    this.displayAuthTokenDialog = true;
  }
}
