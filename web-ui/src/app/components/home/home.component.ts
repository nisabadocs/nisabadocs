import {Component, OnInit} from '@angular/core';
import {SettingsService} from "../../services/settings.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  landingPage: string = '';

  constructor(private settingsService: SettingsService) {
  }

  ngOnInit(): void {
    this.settingsService.fetchLandingPage()
      .subscribe({
        next: data => this.landingPage = data.landingPage
      });
  }
}
