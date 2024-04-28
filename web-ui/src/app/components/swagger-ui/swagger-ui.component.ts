//@ts-nocheck
import {Component, ElementRef, Input, OnChanges, SimpleChanges} from '@angular/core';
import SwaggerUIBundle  from 'swagger-ui-dist/swagger-ui-bundle.js';
import yaml from 'js-yaml';

@Component({
  selector: 'app-swagger-ui',
  templateUrl: './swagger-ui.component.html',
  styleUrls: ['./swagger-ui.component.scss']
})
export class SwaggerUiComponent implements OnChanges {
  @Input() yamlContent: string = '';

  constructor(private el: ElementRef) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['yamlContent'] && this.yamlContent) {
      const spec = yaml.load(this.yamlContent) as any;
      SwaggerUIBundle({
        spec: spec,
        domNode: this.el.nativeElement.querySelector('#swagger-ui'),
        deepLinking: false,
        presets: [SwaggerUIBundle['presets'].apis],
        layout: 'BaseLayout'
      });
    }
  }

}
