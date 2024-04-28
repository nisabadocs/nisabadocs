//@ts-nocheck
export const environment = {
  production: true,
  apiUrl: window['env']['apiUrl'] || "http://localhost:8080",
  keycloak: {
    url: window['env']['keycloakUrl'] || 'http://localhost:18080/auth',
    realm: 'Nisaba',
    clientId: 'nisaba-client',
  }
};
