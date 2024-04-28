import {Project} from "./project.model";

export interface ProjectAuthToken {
  id?: string;
  token?: string;
  name?: string;
  creationDate?: Date;
  lastUsedDate?: Date;
  projectId?: string;
  project?: Project;
}
