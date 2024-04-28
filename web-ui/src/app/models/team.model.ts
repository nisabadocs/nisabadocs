// team.model.ts
export interface Team {
  id?: string;
  name?: string;
  path?: string;
  subGroupCount?: number | null;
  subGroups?: Team[]; // Assuming subGroups have the same structure
  attributes?: { [key: string]: string[] } | null;
  realmRoles?: string[] | null;
  clientRoles?: { [clientId: string]: string[] } | null;
  access?: { [accessType: string]: boolean } | null;
}
