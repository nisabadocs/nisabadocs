import {Project} from "./project.model";

export interface ProjectVersion {
  id: string;
  versionName: string;
  creationDate: Date;
  project: Project;
  isDefault: boolean;
}
