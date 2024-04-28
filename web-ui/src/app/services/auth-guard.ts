import { Injectable } from '@angular/core';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from '@angular/router';
import { KeycloakAuthGuard, KeycloakService } from 'keycloak-angular';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard extends KeycloakAuthGuard {

  constructor(protected override router: Router,
              protected keycloakService: KeycloakService) {
    super(router, keycloakService);
  }

  isAccessAllowed(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean | UrlTree> {
    return new Promise(async (resolve, _) => {
      if (!this.authenticated) {
        this.keycloakService.login();
        return;
      }

      const requiredRoles = route.data['roles'];
      if (!(requiredRoles instanceof Array) || requiredRoles.length === 0) {
        return resolve(true);
      }

      const hasRequiredRoles = requiredRoles.every((role) => this.roles.includes(role));
      if (!hasRequiredRoles) {
        resolve(this.router.parseUrl('/not-found'));
      } else {
        resolve(true);
      }
    });
  }
}
