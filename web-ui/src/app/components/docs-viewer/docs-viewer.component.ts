import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ProjectVersionsService} from "../../services/project-versions.service";
import {ProjectVersionDoc} from "../../models/project-version-doc.model";
import {ProjectVersion} from "../../models/project-version.model";
import {ProjectVersionDocsService} from "../../services/project-version-docs.service";
import yaml from "js-yaml";
import {SettingsService} from "../../services/settings.service";

@Component({
  selector: 'app-docs-viewer',
  templateUrl: './docs-viewer.component.html',
  styleUrl: './docs-viewer.component.scss'
})
export class DocsViewerComponent implements OnInit {
  type: string = 'stoplight'; // default type
  projectSlug: string | null = null;
  versionName: string | null = null;
  docName: string | null = null;
  versionDocs: ProjectVersionDoc[] = [];
  versions: ProjectVersion[] = [];
  baseUrl = '';
  selectedVersion: ProjectVersion | null = null;
  selectedDoc: ProjectVersionDoc | null = null;
  viewerType: string | null = null;

  constructor(private route: ActivatedRoute,
              private projectVersionsService: ProjectVersionsService,
              private projectVersionDocsService: ProjectVersionDocsService,
              private settingsService: SettingsService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.projectSlug = this.route.snapshot.paramMap.get('projectSlug');
    this.viewerType = this.route.snapshot.paramMap.get('viewerType');
    this.versionName = this.route.snapshot.paramMap.get('versionName');
    this.docName = this.route.snapshot.paramMap.get('docName');
    if (!this.projectSlug) {
      return;
    }

    this.updateViewerType();
    this.updateBaseUrl();
    this.fetchProjectVersionDocs();
    this.fetchProjectVersions();
  }

  private fetchProjectVersions() {
    this.projectVersionsService.getProjectVersions(null, this.projectSlug!).subscribe(versions => {
      this.versions = versions;
    });
  }

  private fetchProjectVersionDocs() {
    this.projectVersionDocsService.getProjectVersionDoc(this.projectSlug!, this.versionName!)
      .subscribe({
        next: data => this.processVersionDocs(data),
        error: () => this.router.navigate(['/not-found'])
      });
  }

  private updateViewerType() {
    if (!this.viewerType) {
      this.settingsService.fetchViewer().subscribe({
        next: data => this.type = data.viewer.toLowerCase()
      });
    } else {
      this.type = this.viewerType;
    }
  }

  private updateBaseUrl() {
    this.baseUrl = this.router.createUrlTree(this.getBaseUrlCommands()).toString();
  }

  onVersionChange(version: ProjectVersion) {
    this.versionName = version.versionName;
    this.docName = null;
    this.router.navigate(this.getBaseUrlCommands()).then(() => {
      this.projectVersionDocsService.getProjectVersionDoc(this.projectSlug!, this.versionName!)
        .subscribe(this.processVersionDocs.bind(this));
      this.updateBaseUrl();
    });
  }

  onDocTypeChange(docType: ProjectVersionDoc) {
    this.selectedDoc = docType;
    this.docName = docType.title!;
    this.updateBaseUrl();
    this.router.navigate(this.getBaseUrlCommands());
  }

  private processVersionDocs(data: any): void {
    this.versionDocs = data.map((document: ProjectVersionDoc) => {
      let title;
      try {
        const parsedContent = yaml.load(document.yamlContent!) as any;
        title = parsedContent?.info?.title;
      } catch (error) {
      }

      return {
        ...document,
        title: title || document.fileName
      };
    });
    this.findAndSetSelectedDocuments();
  }

  private findAndSetSelectedDocuments(): void {
    const relevantDocs = this.versionDocs
      .filter(doc => this.versionName ? doc.projectVersion!.versionName === this.versionName : doc.projectVersion!.isDefault);
    this.selectedDoc = relevantDocs
      .find(doc => doc.title === this.docName) || relevantDocs[0];
    this.selectedVersion = this.selectedDoc?.projectVersion || null;
    this.updateBaseUrl();
  }

  goBackToAdmin() {
    const previousUrl = sessionStorage.getItem('previousUrl');
    sessionStorage.removeItem('previousUrl'); // Remove item after retrieval
    this.router.navigateByUrl(previousUrl || '/projects');
  }

  getBaseUrlCommands(): any[] {
    const commands = [];
    if (this.viewerType) {
      commands.push('viewer', this.viewerType);
    }
    commands.push('docs', this.projectSlug)

    if (this.versionName) {
      commands.push('version', this.versionName);
    }
    if (this.docName) {
      commands.push('doc', this.docName);
    }
    return commands;
  }

  isAsyncViewer(): boolean {
    return this.selectedDoc?.yamlContent?.startsWith('asyncapi:') || false;
  }

  isOpenApiViewer(): boolean {
    return this.selectedDoc?.yamlContent?.startsWith('openapi:') || false;
  }

  isMarkDownViewer(): boolean {
    return this.selectedDoc?.fileName?.endsWith('.md') || false;
  }
}
