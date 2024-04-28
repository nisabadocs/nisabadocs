import {ProjectVersion} from "./project-version.model";

export interface ProjectVersionDoc {
  id?: string;
  projectVersion?: ProjectVersion;
  fileName?: string;
  yamlContent?: string;
  title?: string;
  creationDate?: Date;
}
