import { Injectable } from '@angular/core';
import { KeycloakService } from "keycloak-angular";

@Injectable({
  providedIn: 'root'
})
export class RoleCheckerService {
  constructor(private keycloakService: KeycloakService) {
  }

  hasRole(roleName: string): boolean {
    return this.keycloakService.isUserInRole(roleName);
  }

  hasAnyRole(roles: string[]): boolean {
    return roles.some(role => this.hasRole(role));
  }

  // Project roles
  hasProjectCreateRole(): boolean {
    return this.hasRole('per:project:create');
  }

  hasProjectDeleteRole(): boolean {
    return this.hasRole('per:project:delete');
  }

  hasProjectUpdateRole(): boolean {
    return this.hasRole('per:project:update');
  }

  hasProjectViewRole(): boolean {
    return this.hasRole('per:project:view');
  }

  // Project version roles
  hasProjectVersionCreateRole(): boolean {
    return this.hasRole('per:project-version:create');
  }

  hasProjectVersionDeleteRole(): boolean {
    return this.hasRole('per:project-version:delete');
  }

  hasProjectVersionUpdateRole(): boolean {
    return this.hasRole('per:project-version:update');
  }

  hasProjectVersionViewRole(): boolean {
    return this.hasRole('per:project-version:view');
  }

  // Project member roles
  hasProjectMemberAssignRole(): boolean {
    return this.hasRole('per:project-member:assign');
  }

  hasProjectMemberRemoveRole(): boolean {
    return this.hasRole('per:project-member:remove');
  }

  hasProjectMemberViewRole(): boolean {
    return this.hasRole('per:project-member:view');
  }

  // Project token roles
  hasProjectTokenViewRole(): boolean {
    return this.hasRole('per:project-token:view');
  }

  hasProjectTokenCreateRole(): boolean {
    return this.hasRole('per:project-token:create');
  }

  hasProjectTokenDeleteRole(): boolean {
    return this.hasRole('per:project-token:delete');
  }

  // Project documentation roles
  hasProjectDocViewRole(): boolean {
    return this.hasRole('per:project-doc:view');
  }

  // Project team roles
  hasProjectTeamAssignRole(): boolean {
    return this.hasRole('per:project-team:assign');
  }

  hasProjectTeamRemoveRole(): boolean {
    return this.hasRole('per:project-team:remove');
  }

  hasProjectTeamViewRole(): boolean {
    return this.hasRole('per:project-team:view');
  }

  // Team roles
  hasTeamCreateRole(): boolean {
    return this.hasRole('per:team:create');
  }

  hasTeamDeleteRole(): boolean {
    return this.hasRole('per:team:delete');
  }

  hasTeamViewRole(): boolean {
    return this.hasRole('per:team:view');
  }

  hasTeamUpdateRole(): boolean {
    return this.hasRole('per:team:update');
  }

  hasTeamMemberAddRole(): boolean {
    return this.hasRole('per:team-member:add');
  }

  hasTeamMemberRemoveRole(): boolean {
    return this.hasRole('per:team-member:remove');
  }

  hasTeamMemberViewRole(): boolean {
    return this.hasRole('per:team-member:view');
  }

  // Member roles
  hasMemberCreateRole(): boolean {
    return this.hasRole('per:member:create');
  }

  hasMemberDeleteRole(): boolean {
    return this.hasRole('per:member:delete');
  }

  hasMemberViewRole(): boolean {
    return this.hasRole('per:member:view');
  }

  hasMemberAssignRole(): boolean {
    return this.hasRole('per:member:assignRole');
  }

  // settings role
  hasSettingsViewRole(): boolean {
    return this.hasRole('per:settings:view');
  }

  hasSettingsUpdateRole(): boolean {
    return this.hasRole('per:settings:update');
  }
}
