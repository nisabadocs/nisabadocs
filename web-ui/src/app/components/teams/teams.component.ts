import {Component, OnInit} from '@angular/core';
import {TeamService} from "../../services/teams.service";
import {Team} from "../../models/team.model";
import {Router} from "@angular/router";
import {RoleCheckerService} from "../../services/role-checker.service";

@Component({
  selector: 'app-teams',
  templateUrl: './teams.component.html',
  styleUrl: './teams.component.scss'
})
export class TeamsComponent implements OnInit {
  teams: Team[] = [];
  displayDialog: boolean = false;
  newTeam: Team = {
    name: ''
  };

  constructor(private teamsService: TeamService,
              private router: Router,
              public roleChecker: RoleCheckerService) {}

  ngOnInit(): void {
    this.fetchGroups();
  }

  fetchGroups(): void {
    this.teamsService.getTeams().subscribe((teams: Team[]) => {
      this.teams = teams;
    });
  }

  showDialog(): void {
    this.displayDialog = true;
  }

  createTeam(): void {
    this.teamsService.createTeam(this.newTeam).subscribe({
      next: (team) => {
        // The backend should return the newly created team with an ID
        this.teams.push(team);
        this.displayDialog = false;
        this.newTeam = { name: '' }; // Reset the form
      },
      error: (error) => {
        console.error('There was an error creating the team', error);
      }
    });
  }

  goToTeamDetails(teamId: string) {
    this.router.navigate(['/teams', teamId]);
  }

}
