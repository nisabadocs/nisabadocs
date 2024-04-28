import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ProjectAuthToken} from "../models/project-auth-token.model";
import {environment} from "../../environments/environment";

@Injectable({providedIn: 'root'})
export class ProjectAuthService {
  private apiUrl = `${environment.apiUrl}/api/auth-tokens`;

  constructor(private http: HttpClient) {
  }

  listTokensByProjectId(projectId: string): Observable<ProjectAuthToken[]> {
    return this.http.get<ProjectAuthToken[]>(`${this.apiUrl}/project/${projectId}`);
  }

  deleteToken(tokenId: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${tokenId}`);
  }

  createToken(projectId: string, name: string): Observable<ProjectAuthToken> {
    return this.http.post<ProjectAuthToken>(this.apiUrl, {projectId, name});
  }
}
