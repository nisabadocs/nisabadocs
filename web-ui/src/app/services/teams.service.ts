import {Injectable} from '@angular/core';

import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Team} from "../models/team.model";
import {Member} from "../models/member.model";
import {environment} from "../../environments/environment";

@Injectable({providedIn: 'root'})
export class TeamService {

  private apiUrl = `${environment.apiUrl}/api/teams`;

  constructor(private http: HttpClient) {
  }

  getTeams(): Observable<Team[]> {
    return this.http.get<Team[]>(this.apiUrl);
  }

  getTeam(teamId: string): Observable<Team> {
    return this.http.get<Team>(`${this.apiUrl}/${teamId}`);
  }

  createTeam(team: Team): Observable<Team> {
    return this.http.post<Team>(this.apiUrl, team);
  }

  addUserToTeam(teamId: string, members: string[]): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${teamId}/members`, members);
  }

  removeUserFromTeam(teamId: string, userId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${teamId}/members/${userId}`);
  }

  getGroupMembers(teamId: string): Observable<Member[]> {
    return this.http.get<Member[]>(`${this.apiUrl}/${teamId}/members`);
  }

  deleteTeam(teamId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${teamId}`);
  }
}
