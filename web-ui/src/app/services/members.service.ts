import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Member } from '../models/member.model';
import { environment } from '../../environments/environment';
@Injectable({
  providedIn: 'root'
})
export class MembersService {
  private baseUrl = `${environment.apiUrl}/api/members`;  // Adjust as needed

  constructor(private http: HttpClient) {}

  getMembers(): Observable<Member[]> {
    return this.http.get<Member[]>(this.baseUrl);
  }

  createMember(member: Member): Observable<Member> {
    return this.http.post<Member>(this.baseUrl, member);
  }

  searchMembersByEmail(email: string): Observable<Member[]> {
    return this.http.get<Member[]>(`${this.baseUrl}/search?email=${email}`);
  }

  deleteMember(memberId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${memberId}`);
  }

  assignRoleToUser(userId: string, roleId: string): Observable<any> { // Adjust the return type as needed
    return this.http.post(`${this.baseUrl}/${userId}/roles/${roleId}`, {});
  }

}
