import {Component, OnInit, NgZone, Input, ViewEncapsulation} from '@angular/core';

@Component({
  selector: 'app-stoplight-docs',
  templateUrl: './stoplight-docs.component.html',
  styleUrl: './stoplight-docs.component.scss',
  encapsulation: ViewEncapsulation.None,
})
export class StoplightDocsComponent implements OnInit {
  @Input() yamlContent: string = '';
  @Input() url: string = '';
  // apiDescriptionDocument = 'https://api.apis.guru/v2/specs/instagram.com/1.0.0/swagger.yaml';

  ngOnInit() {
    // Ensures Angular detects changes inside the web component
    // this.zone.runOutsideAngular(() => {
    //   customElements.define('elements-api', require('@stoplight/elements').API);
    // });
  }
}
