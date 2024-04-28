import {APP_INITIALIZER, CUSTOM_ELEMENTS_SCHEMA, NgModule, PLATFORM_ID} from '@angular/core';
import {BrowserModule, provideClientHydration} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {RouterLink, RouterOutlet} from "@angular/router";
import {StoplightDocsComponent} from "./components/stoplight-docs/stoplight-docs.component";
import {RedocComponent} from "./components/redoc/redoc.component";
import {SwaggerUiComponent} from "./components/swagger-ui/swagger-ui.component";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {AppLayoutModule} from "./layout/app.layout.module";
import {ProjectsComponent} from './components/projects/projects.component';
import {TableModule} from "primeng/table";
import {DialogModule} from "primeng/dialog";
import {FormsModule} from "@angular/forms";
import {ButtonModule} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {RippleModule} from "primeng/ripple";
import {DocsViewerComponent} from './components/docs-viewer/docs-viewer.component';
import {TooltipModule} from "primeng/tooltip";
import {initializeKeycloak} from "./services/keycloak-init.factory";
import {KeycloakAngularModule, KeycloakService} from "keycloak-angular";
import {provideAnimationsAsync} from "@angular/platform-browser/animations/async";
import {TeamsComponent} from './components/teams/teams.component';
import {MembersComponent} from './components/members/members.component';
import {CacheControlInterceptor} from "./no-cache.interceptor";
import {TeamDetailsComponent} from './components/team-details/team-details.component';
import {AutoCompleteModule} from "primeng/autocomplete";
import {ConfirmDialogModule} from 'primeng/confirmdialog'
import {ConfirmPopupModule} from "primeng/confirmpopup";
import {ConfirmationService, MessageService} from 'primeng/api';
import {ProjectDetailsComponent} from './components/project-details/project-details.component';
import {DropdownModule} from "primeng/dropdown";
import {ListboxModule} from "primeng/listbox";
import {FieldsetModule} from "primeng/fieldset";
import {MomentModule} from "ngx-moment";
import {MultiSelectModule} from "primeng/multiselect";
import {MessagesModule} from 'primeng/messages';
import {MessageModule} from 'primeng/message';
import {HttpErrorInterceptorService} from './http-error-interceptor.service';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {ToastModule} from "primeng/toast";
import {NotfoundComponent} from "./components/notfound/notfound.component";
import {InternalNotfoundComponent} from "./components/internal-notfound/internal-notfound.component";
import {SettingsComponent} from './components/settings/settings.component';
import {SplitButtonModule} from "primeng/splitbutton";
import { HomeComponent } from './components/home/home.component';
import { MarkdownModule } from 'ngx-markdown';
import {InputTextareaModule} from "primeng/inputtextarea";

@NgModule({
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  declarations: [
    AppComponent,
    StoplightDocsComponent,
    RedocComponent,
    SwaggerUiComponent,
    ProjectsComponent,
    DocsViewerComponent,
    TeamsComponent,
    MembersComponent,
    TeamDetailsComponent,
    ProjectDetailsComponent,
    NotfoundComponent,
    InternalNotfoundComponent,
    SettingsComponent,
    HomeComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    RouterLink,
    RouterOutlet,
    RouterOutlet,
    RouterLink,
    HttpClientModule,
    AppLayoutModule,
    TableModule,
    DialogModule,
    FormsModule,
    ButtonModule,
    InputTextModule,
    RippleModule,
    TooltipModule,
    KeycloakAngularModule,
    AutoCompleteModule,
    ConfirmDialogModule,
    ConfirmPopupModule,
    DropdownModule,
    ListboxModule,
    FieldsetModule,
    MomentModule,
    MultiSelectModule,
    MessagesModule,
    MessageModule,
    ToastModule,
    SplitButtonModule,
    MarkdownModule.forRoot(),
    InputTextareaModule
  ],
  providers: [
    ConfirmationService,
    BrowserAnimationsModule,
    ToastModule,
    MessagesModule,
    MessageModule,
    provideClientHydration(),
    provideAnimationsAsync(),
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService, PLATFORM_ID],
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: CacheControlInterceptor,
      multi: true
    },
    {provide: HTTP_INTERCEPTORS, useClass: HttpErrorInterceptorService, multi: true},
    MessageService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
