import {Component, OnInit} from '@angular/core';
import {RoleCheckerService} from "../../services/role-checker.service";
import {ApplicationSetting} from "../../models/settings.model";
import {SettingsService} from "../../services/settings.service";

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss'
})
export class SettingsComponent implements OnInit {
  logo: ApplicationSetting = {settingKey: 'logo', settingValue: ''};
  defaultViewer: ApplicationSetting = {settingKey: 'default-viewer', settingValue: ''};
  landingPage: ApplicationSetting = {settingKey: 'landing-page', settingValue: ''};

  viewerSelections = [
    {label: 'Stoplight', value: 'Stoplight'},
    {label: 'Swagger', value: 'Swagger'},
    {label: 'Redoc', value: 'Redoc'}
  ];

  constructor(
    public roleChecker: RoleCheckerService,
    private settingsService: SettingsService
  ) {
  }

  ngOnInit(): void {
    // Call the getSettings method from SettingsService and subscribe to the result
    this.settingsService.getSettings().subscribe({
      next: (settings: ApplicationSetting[]) => {
        // Find the logo setting in the settings array
        const logo = settings.find(setting => setting.settingKey === 'logo');
        if (logo) {
          this.logo = logo;
        }
        const defaultViewer = settings.find(setting => setting.settingKey === 'default-viewer');
        if (defaultViewer) {
          this.defaultViewer = defaultViewer;
        }

        const landingPage = settings.find(setting => setting.settingKey === 'landing-page');
        if (landingPage) {
          this.landingPage = landingPage;
        }
      }
    });
  }

  saveLogoUrl() {
    // Call the saveLogoSetting method from SettingsService and subscribe to the result
    this.settingsService.saveSettings([this.logo, this.defaultViewer, this.landingPage]).subscribe({
      next: (setting: ApplicationSetting) => {
        // Handle the successful save here
        console.log('Logo URL saved successfully', setting);
      },
      error: (error) => {
        // Handle any errors here
        console.error('Error saving logo URL', error);
      }
    });
  }

}
