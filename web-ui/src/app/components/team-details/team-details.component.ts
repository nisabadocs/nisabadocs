import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Member} from '../../models/member.model';
import {Team} from '../../models/team.model';
import {MembersService} from '../../services/members.service';
import {TeamService} from '../../services/teams.service';
import {ConfirmationService} from 'primeng/api';
import {forkJoin} from "rxjs";
import {RoleCheckerService} from "../../services/role-checker.service";

@Component({
  selector: 'app-team-details',
  templateUrl: './team-details.component.html',
  styleUrls: ['./team-details.component.scss']
})
export class TeamDetailsComponent implements OnInit {
  team?: Team;
  members: Member[] = [];
  displayAddMemberDialog: boolean = false;
  filteredMembers: Member[] = [];
  availableMembers: Member[] = [];
  selectedMembers: Member[] = [];

  constructor(
    private route: ActivatedRoute,
    private teamsService: TeamService,
    private membersService: MembersService,
    private confirmationService: ConfirmationService,
    private router: Router,
    public roleChecker: RoleCheckerService
  ) {
  }

  ngOnInit(): void {
    this.loadTeamDetails();
  }

  loadTeamDetails(): void {
    const teamId = this.route.snapshot.paramMap.get('id');
    if (teamId) {
      this.teamsService.getTeam(teamId).subscribe(team => {
        this.team = team;
        this.loadTeamMembers();
      }, _ => {
        this.router.navigate(['/404']);
      });
    }
  }

  loadTeamMembers(): void {
    const teamId = this.route.snapshot.paramMap.get('id');
    if (teamId) {
      forkJoin([
        this.membersService.getMembers(),
        this.teamsService.getGroupMembers(teamId)
      ]).subscribe({
        next: ([allMembers, teamMembers]) => {
          allMembers = allMembers.filter(member => member.email);
          this.members = teamMembers;
          this.availableMembers = allMembers.filter(member => !this.members.some(m => m.id === member.id));
        }
      });
    }
  }

  showAddMemberDialog(): void {
    this.displayAddMemberDialog = true;
  }

  searchMembers(event: any) {
    this.membersService.searchMembersByEmail(event.query)
      .subscribe(members => this.filteredMembers = members);
  }

  addMember() {
    if (this.selectedMembers && this.team?.id) {
      const memberIds = this.selectedMembers.map(member => member.id!);
      this.teamsService.addUserToTeam(this.team.id, memberIds).subscribe(() => {
        // Successfully added the member
        this.loadTeamMembers(); // Refresh the member list
        this.displayAddMemberDialog = false;
        this.selectedMembers = []; // Reset search
      });
    }
  }

  removeMember(member: Member) {
    this.confirmationService.confirm({
      message: `Are you sure you want to remove ${member.email} from the team?`,
      accept: () => {
        if (this.team?.id) {
          this.teamsService.removeUserFromTeam(this.team.id, member.id!).subscribe(() => {
            this.loadTeamMembers(); // Refresh the member list
          });
        }
      }
    });
  }

  confirmDelete(team: Team) {
    this.confirmationService.confirm({
      message: `Are you sure you want to delete ${team.name}?`,
      accept: () => {
        this.deleteTeam(team.id!);
      }
    });
  }

  deleteTeam(teamId: string): void {
    this.teamsService.deleteTeam(teamId).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (error) => {
        console.error('Error deleting team:', error);
      }
    });
  }
}
