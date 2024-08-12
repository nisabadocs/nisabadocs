import {Component, Input, OnChanges} from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { MarkdownService } from 'ngx-markdown';

@Component({
  selector: 'app-markdown-viewer',
  templateUrl: './markdown-viewer.component.html',
  styleUrl: './markdown-viewer.component.scss'
})
export class MarkdownViewerComponent implements OnChanges {
  @Input() content: string = '';
  sanitizedContent: SafeHtml;

  constructor(private markdownService: MarkdownService, private sanitizer: DomSanitizer) {
    this.sanitizedContent = this.sanitizer.bypassSecurityTrustHtml('');
  }

  ngOnChanges() {
    this.sanitizedContent = this.sanitizer.bypassSecurityTrustHtml(
      <string>this.markdownService.parse(this.content)
    );
  }
}
