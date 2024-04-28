import {Component, OnInit} from '@angular/core';
import {Member} from '../../models/member.model';
import {MembersService} from '../../services/members.service';
import {ConfirmationService} from "primeng/api";
import {RolesService} from "../../services/roles.service";
import {Role} from "../../models/role.model";
import {RoleCheckerService} from "../../services/role-checker.service"; // Assume you'll create this service

@Component({
  selector: 'app-members',
  templateUrl: './members.component.html',
  styleUrls: ['./members.component.scss']
})
export class MembersComponent implements OnInit {
  members: Member[] = [];
  displayDialog: boolean = false;
  newMember: Member = {id: '', username: ''};
  roles: Role[] = [];
  roleNames: String[] = [];


  constructor(private membersService: MembersService,
              private rolesService: RolesService,
              private confirmationService: ConfirmationService,
              public roleChecker: RoleCheckerService) {
  }

  ngOnInit(): void {
    this.fetchMembers();
    this.fetchRoles();
  }

  fetchMembers(): void {
    this.membersService.getMembers().subscribe((members: Member[]) => {
      this.members = members;
    });
  }

  fetchRoles(): void {
    this.rolesService.getRoles().subscribe((roles) => {
      this.roles = roles;
      this.roleNames = roles.map((role) => role.description || '');
    });
  }

  assignRoleToUser(userId: string, roleDescription: string): void {
    const role = this.roles.find((role) => role.description === roleDescription);
    if (!role) {
      return;
    }
    const roleName = role.name!;
    this.membersService.assignRoleToUser(userId, roleName).subscribe({
      next: () => {
        console.log(`Role ${roleName} assigned to user ${userId}`);
        this.fetchMembers();  // Refresh the list
        // Optionally refresh members list or update UI as needed
      },
      error: (error) => {
        console.error('Error assigning role:', error);
      }
    });
  }

  showDialog(): void {
    this.displayDialog = true;
  }

  createMember(): void {
    this.newMember.username = this.newMember.email;  // Assume username is the same as email
    this.membersService.createMember(this.newMember).subscribe({
      next: (_) => {
        this.fetchMembers();  // Refresh the list
        this.displayDialog = false;
        this.newMember = {id: '', username: '', email: ''};  // Reset for next use
      },
      error: (error) => {
        console.error('Error creating member:', error);
      }
    });
  }

  confirmDelete(member: Member) {
    this.confirmationService.confirm({
      message: `Are you sure you want to delete ${member.email}?`,
      accept: () => {
        this.deleteMember(member.id!);
      }
    });
  }

  deleteMember(memberId: string) {
    this.membersService.deleteMember(memberId).subscribe({
      next: () => {
        this.fetchMembers(); // Refresh the list after deletion
      },
      error: (error) => {
        console.error('Error deleting member:', error);
      }
    });
  }
}
