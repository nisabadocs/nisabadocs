import {Component, ElementRef, Input, OnChanges, SimpleChanges} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import yaml from 'js-yaml';

declare const Redoc: any; // Declare Redoc to avoid TypeScript errors

@Component({
  selector: 'app-redoc',
  templateUrl: './redoc.component.html',
  styleUrls: ['./redoc.component.scss'] // Corrected styleUrl to styleUrls
})
export class RedocComponent implements OnChanges {
  @Input() yamlContent: string = '';

  constructor(private elementRef: ElementRef, private http: HttpClient) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['yamlContent'] && this.yamlContent) {
      Redoc.init(yaml.load(this.yamlContent), {}, document.getElementById('redoc-container'));
    }
  }


}
