import {Injectable} from '@angular/core';

import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ProjectVersionDoc} from "../models/project-version-doc.model";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ProjectVersionDocsService {
  private apiUrl = `${environment.apiUrl}/api/project-docs`;

  constructor(private http: HttpClient) {
  }

  getProjectVersionDoc(slug: string, versionName: string): Observable<ProjectVersionDoc[]> {
    let requestBody = {slug: slug, versionName: ''};
    if (versionName) {
      const decodedVersionName = decodeURIComponent(versionName!);
      requestBody = {...requestBody, versionName: decodedVersionName};
    }
    return this.http.post<ProjectVersionDoc[]>(this.apiUrl, requestBody);
  }


}
