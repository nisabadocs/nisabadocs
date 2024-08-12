import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AppLayoutComponent} from "./layout/app.layout.component";
import {ProjectsComponent} from "./components/projects/projects.component";
import {DocsViewerComponent} from "./components/docs-viewer/docs-viewer.component";
import {AuthGuard} from "./services/auth-guard";
import {TeamsComponent} from "./components/teams/teams.component";
import {MembersComponent} from "./components/members/members.component";
import {TeamDetailsComponent} from "./components/team-details/team-details.component";
import {ProjectDetailsComponent} from "./components/project-details/project-details.component";
import {NotfoundComponent} from "./components/notfound/notfound.component";
import {InternalNotfoundComponent} from "./components/internal-notfound/internal-notfound.component";
import {SettingsComponent} from "./components/settings/settings.component";
import {HomeComponent} from "./components/home/home.component";

const routes: Routes = [
  {
    path: '',
    component: AppLayoutComponent,
    children: [
      {path: '', component: HomeComponent},
      {path: 'projects', component: ProjectsComponent},
      {
        path: 'projects/:projectId',
        component: ProjectDetailsComponent,
        data: {roles: ['per:project:view']},
        canActivate: [AuthGuard]
      },
      {
        path: 'teams',
        component: TeamsComponent,
        data: {roles: ['per:team:view']},
        canActivate: [AuthGuard]
      },
      {
        path: 'teams/:id',
        component: TeamDetailsComponent,
        data: {roles: ['per:team:update']},
        canActivate: [AuthGuard]
      },
      {path: 'members', component: MembersComponent, data: {roles: ['per:member:view']}, canActivate: [AuthGuard]},
      {path: 'settings', component: SettingsComponent, data: {roles: ['per:settings:view']}, canActivate: [AuthGuard]},
      {path: '404', component: InternalNotfoundComponent},
    ]
  },
  {
    path: 'viewer/:viewerType/docs/:projectSlug/version/:versionName/doc/:docName',
    component: DocsViewerComponent,
    children: [
      {path: '**', component: DocsViewerComponent}
    ]
  },
  {
    path: 'viewer/:viewerType/docs/:projectSlug/version/:versionName',
    component: DocsViewerComponent,
    children: [
      {path: '**', component: DocsViewerComponent}
    ]
  },
  {
    path: 'viewer/:viewerType/docs/:projectSlug',
    component: DocsViewerComponent,
    children: [
      {path: '**', component: DocsViewerComponent,}
    ]
  },
  {
    path: 'docs/:projectSlug/version/:versionName/doc/:docName',
    component: DocsViewerComponent,
    children: [
      {path: '**', component: DocsViewerComponent}
    ]
  },
  {
    path: 'docs/:projectSlug/doc/:docName',
    component: DocsViewerComponent,
    children: [
      {path: '**', component: DocsViewerComponent}
    ]
  },
  {
    path: 'docs/:projectSlug/version/:versionName',
    component: DocsViewerComponent,
    children: [
      {path: '**', component: DocsViewerComponent}
    ]
  },
  {
    path: 'docs/:projectSlug',
    component: DocsViewerComponent,
    children: [
      {path: '**', component: DocsViewerComponent}
    ]
  },
  {path: 'not-found', component: NotfoundComponent},
  {path: '**', redirectTo: '/not-found'}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
