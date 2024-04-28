// keycloak-init.factory.ts
import {KeycloakService} from 'keycloak-angular';
import {environment} from "../../environments/environment";

function initializeKeycloak(keycloak: KeycloakService) {
  return () => {
    try {
      return keycloak.init({
        config: {
          url: environment.keycloak.url,
          realm: environment.keycloak.realm,
          clientId: environment.keycloak.clientId
        },
        initOptions: {
          onLoad: 'check-sso',
          checkLoginIframe: false,
        }
      });
    } catch (e) {
      console.error('Error initializing Keycloak', e);
      return Promise.resolve(false);
    }
  }
}

export {initializeKeycloak};
