import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {Role} from "../models/role.model";
@Injectable({
  providedIn: 'root'
})
export class RolesService {
  private baseUrl = `${environment.apiUrl}/api/roles`;  // Adjust as needed

  constructor(private http: HttpClient) {}

  getRoles(): Observable<Role[]> { // Adjust the return type based on your data structure
    return this.http.get<Role[]>(this.baseUrl);
  }

}
