import {Injectable} from '@angular/core';

import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Project} from '../models/project.model';
import {Team} from "../models/team.model";
import {Member} from "../models/member.model"; // Assume you have a Project model
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = `${environment.apiUrl}/api/projects`; // Adjust if your API URL is different

  constructor(private http: HttpClient) {
  }

  getPublicProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.apiUrl}/public`);
  }

  getProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(this.apiUrl);
  }

  createProject(project: Project): Observable<Project> {
    return this.http.post<Project>(this.apiUrl, project);
  }

  getProjectDetails(projectId: string): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/${projectId}`);
  }

  getProjectTeams(projectId: string): Observable<Team[]> {
    return this.http.get<Team[]>(`${this.apiUrl}/${projectId}/teams`);
  }

  getProjectMembers(projectId: string): Observable<Member[]> {
    return this.http.get<Member[]>(`${this.apiUrl}/${projectId}/members`);
  }

  assignTeamToProject(projectId: string, teamIds?: string[]): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${projectId}/teams`, teamIds);
  }

  removeTeamFromProject(projectId: string, teamId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${projectId}/teams/${teamId}`);
  }

  assignMemberToProject(projectId: string, memberIds?: string[]): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${projectId}/members`, memberIds);
  }

  removeMemberFromProject(projectId: string, memberId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${projectId}/members/${memberId}`);
  }

  deleteProject(projectId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${projectId}`);
  }

  updateProject(projectId: string, updateData: { name: string, slug: string }): Observable<Project> {
    return this.http.patch<Project>(`${this.apiUrl}/${projectId}`, updateData);
  }
}
