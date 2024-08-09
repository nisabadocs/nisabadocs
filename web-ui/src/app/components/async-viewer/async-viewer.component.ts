import {Component, Input} from '@angular/core';
import '@asyncapi/web-component/lib/asyncapi-web-component';

@Component({
  selector: 'app-async-viewer',
  templateUrl: './async-viewer.component.html',
  styleUrl: './async-viewer.component.scss'
})
export class AsyncViewerComponent {
  @Input() yamlContent: string = '';
}

