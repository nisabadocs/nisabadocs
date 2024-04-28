import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {MenuItem} from 'primeng/api';
import {LayoutService} from "./service/app.layout.service";
import {KeycloakService} from "keycloak-angular";
import {SettingsService} from "../services/settings.service";

@Component({
  selector: 'app-topbar',
  templateUrl: './app.topbar.component.html'
})
export class AppTopBarComponent implements OnInit {

  items!: MenuItem[];

  @ViewChild('menubutton') menuButton!: ElementRef;

  @ViewChild('topbarmenubutton') topbarMenuButton!: ElementRef;

  @ViewChild('topbarmenu') menu!: ElementRef;

  logoUrl?: string;


  constructor(public layoutService: LayoutService,
              public keycloakService: KeycloakService,
              private settingsService: SettingsService) {

  }

  ngOnInit() {
    this.settingsService.fetchLogoUrl().subscribe({
      next: (data) => {
        this.logoUrl = data.logo;
      }
    });
    this.items = [
      {
        label: 'Logout',
        icon: 'pi pi-sign-out',
        command: () => {
          this.logout();
        }
      }
    ];
  }

  logout() {
    this.keycloakService.logout();
  }

  login() {
    this.keycloakService.login();
  }

}
