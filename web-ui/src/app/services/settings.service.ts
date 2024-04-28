// settings.service.ts

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApplicationSetting} from "../models/settings.model";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class SettingsService {

  private apiUrl = `${environment.apiUrl}/api/settings`;

  constructor(private http: HttpClient) {
  }

  getSettings(): Observable<ApplicationSetting[]> {
    return this.http.get<ApplicationSetting[]>(this.apiUrl);
  }

  fetchLogoUrl(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/logo`);
  }

  fetchViewer(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/viewer`);
  }

  fetchLandingPage(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/landing-page`);
  }

  saveSettings(settings: ApplicationSetting[]): Observable<any> {
    return this.http.post<ApplicationSetting>(this.apiUrl, settings);
  }
}
