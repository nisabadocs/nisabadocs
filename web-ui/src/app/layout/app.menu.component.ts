import {Component, OnInit} from '@angular/core';
import {RoleCheckerService} from "../services/role-checker.service";

@Component({
  selector: 'app-menu',
  templateUrl: './app.menu.component.html'
})
export class AppMenuComponent implements OnInit {

  model: any[] = [];

  constructor(private roleCheckerService: RoleCheckerService) {
  }

  ngOnInit() {
    this.model = [
      {
        label: 'Home',
        items: []
      }
    ];
    this.model[0].items.push({label: 'Home', icon: 'pi pi-fw pi-home', routerLink: ['/']});
    this.model[0].items.push({label: 'Projects', icon: 'pi pi-fw pi-briefcase', routerLink: ['/projects']});
    if (this.roleCheckerService.hasMemberViewRole()) {
      this.model[0].items.push({label: 'Members', icon: 'pi pi-fw pi-user', routerLink: ['/members']});
    }
    if (this.roleCheckerService.hasTeamViewRole()) {
      this.model[0].items.push({label: 'Teams', icon: 'pi pi-fw pi-users', routerLink: ['/teams']});
    }
    if (this.roleCheckerService.hasSettingsViewRole()) {
      this.model[0].items.push({label: 'Settings', icon: 'pi pi-fw pi-cog', routerLink: ['/settings']});
    }
  }
}
