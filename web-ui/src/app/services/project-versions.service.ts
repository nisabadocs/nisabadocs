import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {ProjectVersion} from "../models/project-version.model";
import {environment} from "../../environments/environment";


@Injectable({
  providedIn: 'root'
})
export class ProjectVersionsService {

  private apiUrl = `${environment.apiUrl}/api/project-versions`;

  constructor(private http: HttpClient) {
  }

  getProjectVersions(projectId?: string| null, slug?: string): Observable<ProjectVersion[]> {
    let params = new HttpParams();
    if (projectId) { params = params.set('projectId', projectId); }
    if (slug) { params = params.set('slug', slug); }

    // Construct the correct URL
    const urlWithParams = params.keys().length === 0
      ? `${this.apiUrl}`
      : `${this.apiUrl}?${params.toString()}`;

    return this.http.get<ProjectVersion[]>(urlWithParams);
  }

  updateDefaultVersion(versionId: string): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${versionId}`,
      {isDefault: true},
      {headers: new HttpHeaders({'Content-Type': 'application/json'})});
  }

  deleteProjectVersion(versionId: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${versionId}`);
  }
}
