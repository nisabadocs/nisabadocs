--liquibase formatted sql

--changeset nisaba:1
CREATE TABLE project
(
    id               UUID PRIMARY KEY NOT NULL default gen_random_uuid(),
    name             VARCHAR(255)     NOT NULL,
    slug             VARCHAR(255)     NOT NULL UNIQUE,
    deleted          BOOLEAN                   DEFAULT FALSE,
    visibility       VARCHAR(255)     NOT NULL DEFAULT 'INTERNAL',
    creation_date    TIMESTAMP WITH TIME ZONE  DEFAULT CURRENT_TIMESTAMP,
    last_update_date TIMESTAMP WITH TIME ZONE  DEFAULT CURRENT_TIMESTAMP
);

--changeset nisaba:2
CREATE INDEX idx_project_slug ON project (slug);

--changeset nisaba:3
CREATE INDEX idx_project_visibility ON project (visibility);

--changeset nisaba:4
CREATE TABLE project_version
(
    id               UUID PRIMARY KEY NOT NULL default gen_random_uuid(),
    project_id       UUID             NOT NULL,
    version_name     VARCHAR(255)     NOT NULL,
    creation_date    TIMESTAMP WITH TIME ZONE  DEFAULT CURRENT_TIMESTAMP,
    last_update_date TIMESTAMP WITH TIME ZONE  DEFAULT CURRENT_TIMESTAMP,
    is_default       BOOLEAN                   DEFAULT FALSE,
    FOREIGN KEY (project_id) REFERENCES project (id)
);

--changeset nisaba:5
CREATE INDEX idx_project_version_project_id ON project_version (project_id);

--changeset nisaba:6
CREATE INDEX idx_project_version_name ON project_version (version_name);

--changeset nisaba:7
CREATE TABLE project_version_doc
(
    id                 UUID PRIMARY KEY NOT NULL default gen_random_uuid(),
    project_version_id UUID             NOT NULL,
    file_name          VARCHAR(255)     NOT NULL,
    yaml_content       TEXT             NOT NULL,
    creation_date      TIMESTAMP WITH TIME ZONE  DEFAULT CURRENT_TIMESTAMP,
    last_update_date   TIMESTAMP WITH TIME ZONE  DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_version_id) REFERENCES project_version (id)
);

--changeset nisaba:8
CREATE INDEX idx_project_version_doc_project_version_id ON project_version_doc (project_version_id);

--changeset nisaba:9
CREATE TABLE project_auth_token
(
    id               UUID PRIMARY KEY NOT NULL default gen_random_uuid(),
    project_id       UUID             NOT NULL,
    name             VARCHAR(255)     NOT NULL,
    auth_token       VARCHAR(255)     NOT NULL,
    creation_date    TIMESTAMP WITH TIME ZONE  DEFAULT CURRENT_TIMESTAMP,
    last_update_date TIMESTAMP WITH TIME ZONE  DEFAULT CURRENT_TIMESTAMP,
    last_used_date   TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (project_id) REFERENCES project (id)
);

--changeset nisaba:10
CREATE INDEX idx_project_auth_token_project_id ON project_auth_token (project_id);

--changeset nisaba:11
CREATE INDEX idx_project_auth_token ON project_auth_token (auth_token);

--changeset nisaba:12
CREATE TABLE project_member
(
    id         UUID PRIMARY KEY NOT NULL default gen_random_uuid(),
    project_id UUID             NOT NULL,
    member_id  VARCHAR(255)     NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project (id)
);

--changeset nisaba:13
CREATE INDEX idx_project_member_project_id ON project_member (project_id);

--changeset nisaba:14
CREATE TABLE project_team
(
    id         UUID PRIMARY KEY NOT NULL default gen_random_uuid(),
    project_id UUID             NOT NULL,
    team_id    VARCHAR(255)     NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project (id)
);

--changeset nisaba:15
CREATE INDEX idx_project_team_project_id ON project_team (project_id);

--changeset nisaba:16

CREATE TABLE settings
(
    id            UUID PRIMARY KEY    NOT NULL default gen_random_uuid(),
    setting_key   VARCHAR(255) UNIQUE NOT NULL,
    setting_value TEXT                NULL     DEFAULT NULL,
    setting_type  VARCHAR(50)         NULL     DEFAULT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE  DEFAULT CURRENT_TIMESTAMP
);


--changeset nisaba:17
INSERT INTO settings (setting_key, setting_value, setting_type)
VALUES ('logo', '', 'STRING');

--changeset nisaba:18
INSERT INTO settings (setting_key, setting_value, setting_type)
VALUES ('default-viewer', 'Stoplight', 'STRING');

--changeset nisaba:19
INSERT INTO settings (setting_key, setting_value, setting_type)
VALUES ('landing-page', '# API Docs', 'STRING');
