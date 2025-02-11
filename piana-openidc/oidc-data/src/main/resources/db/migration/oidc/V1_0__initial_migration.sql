DROP TABLE IF EXISTS client CASCADE;

DROP SEQUENCE IF EXISTS serial_sequence;

CREATE SEQUENCE serial_sequence
    INCREMENT 1
START 10000;

--drop table if exists permissions;
--drop table if exists roles;
--drop table if exists domains;
--drop table if exists credential_types;
--drop table if exists domains_credential_type_order;
--drop table if exists domains_roles_permissions;
--drop table if exists oidc_users;
--drop table if exists domains_users;
--drop table if exists users_domains_roles;
--drop table if exists credential_types;
--drop table if exists password_credential;
--drop table if exists mobile_otp_credential;
--drop table if exists email_otp_credential;

CREATE TABLE connectivity_channel (
    id                          BIGSERIAL       NOT NULL PRIMARY KEY,
    name                        varchar(10)     NOT NULL,
    CONSTRAINT connectivity_channel_f_name_uk UNIQUE (name)
);

ALTER SEQUENCE "connectivity_channel_id_seq" RESTART WITH 100;

insert into connectivity_channel (id, name)
    select 1, 'WEB' union all
    select 2, 'MOBILE' union all
    select 3, 'TCP'
    WHERE NOT EXISTS (SELECT * FROM connectivity_channel);

CREATE TABLE oidc_users
(
    id                          BIGSERIAL               NOT NULL PRIMARY KEY,
    first_name                  VARCHAR(64)             NOT NULL,
    last_name                   VARCHAR(64)             NOT NULL,
    username                    VARCHAR(64)             NOT NULL,
    multi_login_enabled         BOOLEAN                 NOT NULL DEFAULT TRUE,
    create_on                   TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT oidc_users_f_username_uk UNIQUE (username)
);

ALTER SEQUENCE "oidc_users_id_seq" RESTART WITH 100;

insert into oidc_users (id, username, first_name, last_name)
    select 1, 'sysadmin', 'sa_first', 'sa_last' union all
    select 2, 'dadmin1', 'da_first', 'da_last' union all
    select 3, 'user1', 'u_first', 'u_last'
    WHERE NOT EXISTS (SELECT * FROM oidc_users);

CREATE TABLE user_entrance
(
    username                    VARCHAR(64)             NOT NULL PRIMARY KEY,
    failed_try_count            INT                     NOT NULL DEFAULT 0,
    rate                        DOUBLE PRECISION        NOT NULL DEFAULT 0,
    volume                      INT                     NOT NULL DEFAULT 0,
    create_on                   TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT user_entrance_f_username_uk UNIQUE (username)
);

CREATE TABLE permission_type (
    id                          BIGSERIAL       NOT NULL PRIMARY KEY,
    name                        varchar(10)     NOT NULL,
    CONSTRAINT permission_type_f_name_uk UNIQUE (name)
);

insert into permission_type (id, name)
    select 1, 'CREATE' union all
    select 2, 'READ' union all
    select 3, 'UPDATE' union all
    select 4, 'DELETE' union all
    select 5, 'UI'
    WHERE NOT EXISTS (SELECT * FROM permission_type);

CREATE TABLE permissions
(
    id                          BIGSERIAL       NOT NULL PRIMARY KEY,
    permission_type_id          BIGINT          NOT NULL,
    name                        VARCHAR(64)     NOT NULL,
    description                 VARCHAR(256),
    start_hours                 CHAR(5) DEFAULT 0,
    end_hours                   CHAR(5) DEFAULT 24,
    disable                     BOOLEAN         NOT NULL DEFAULT FALSE,
    create_on                   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT permissions_f_name_uk UNIQUE (name),
    CONSTRAINT permissions_f_permission_type_id_fk foreign key(permission_type_id) REFERENCES permission_type (id)
);

ALTER SEQUENCE "permissions_id_seq" RESTART WITH 100;

insert into permissions (id, permission_type_id, name)
    select 1, 5, 'PERM_SYSADMIN_VIEW' union all
    select 2, 5, 'PERM_DOMAIN_ADMIN_VIEW' union all
    select 3, 5, 'PERM_USER_VIEW' union all
    select 11, 1, 'PERM_ADD_USER' union all
    select 12, 1, 'PERM_ADD_DOMAIN' union all
    select 13, 1, 'PERM_ADD_ROLE' union all
    select 14, 1, 'PERM_ADD_PERM_TO_ROLE' union all
    select 15, 1, 'PERM_ADD_ROLE_TO_USER' union all
    select 16, 3, 'PERM_CHANGE_PASSWORD' union all
    select 17, 5, 'PERM_DOMAINS_PAGE' union all
    select 18, 5, 'PERM_ROLES_PAGE' union all
    select 19, 5, 'PERM_PERMISSIONS_PAGE' union all
    select 20, 5, 'PERM_USERS_PAGE' union all
    select 21, 2, 'PERM_READ_USER_PERMISSIONS'
    WHERE NOT EXISTS (SELECT * FROM permissions);

CREATE TABLE roles
(
    id                          BIGSERIAL       NOT NULL PRIMARY KEY,
    name                        VARCHAR(64)     NOT NULL,
    domain_id                   BIGINT,
    inserter_user_id            BIGINT          NOT NULL,
    description                 VARCHAR(256),
    create_on                   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT roles_f_name_uk UNIQUE (name),
    CONSTRAINT roles_f_user_id_fk foreign key(inserter_user_id) REFERENCES oidc_users (id)
);

ALTER SEQUENCE "roles_id_seq" RESTART WITH 100;

insert into roles (id, name, inserter_user_id, description)
    select 1, 'sysadmin', 1, 'system admin role' union all
    select 2, 'domain-admin', 1, 'domain admin role' union all
    select 3, 'domain-user', 1, 'domain user role' union all
    select 4, 'authenticated-user', 1, 'authenticated user role'
    WHERE NOT EXISTS (SELECT * FROM roles);

CREATE TABLE domains
(
    id                          BIGSERIAL       NOT NULL PRIMARY KEY,
    name                        VARCHAR(64)     NOT NULL,
    description                 VARCHAR(256),
    create_on                   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT domains_f_name_uk UNIQUE (name)
);

ALTER SEQUENCE "domains_id_seq" RESTART WITH 100;

insert into domains (id, name, description)
    select 1, 'master', 'authenticate and authorized console'
    WHERE NOT EXISTS (SELECT * FROM domains);

CREATE TABLE domains_roles_permissions
(
    domain_id                   BIGINT                  NOT NULL,
    role_id                     BIGINT                  NOT NULL,
    permission_id               BIGINT                  NOT NULL,
    create_on                   TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT domains_roles_permissions_pk PRIMARY KEY (domain_id, role_id, permission_id),
    CONSTRAINT domains_roles_permissions_f_domain_id_fk
        foreign key (domain_id) references domains(id),
    CONSTRAINT domains_roles_permissions_f_role_id_fk
        foreign key (role_id) references roles(id),
    CONSTRAINT domains_roles_permissions_f_permission_id_fk
        foreign key (permission_id) references permissions(id)
);

insert into domains_roles_permissions (domain_id, role_id, permission_id)
    select 1, 1, p.id from permissions p union all
    select 1, 2, 2 union all
    select 1, 2, 3 union all
    select 1, 2, 18 union all
    select 1, 2, 19 union all
    select 1, 2, 20 union all
    select 1, 4, 16
    WHERE NOT EXISTS (SELECT * FROM domains_roles_permissions);

CREATE TABLE domains_users
(
    user_id                     BIGINT                  NOT NULL,
    domain_id                   BIGINT                  NOT NULL,
    create_on                   TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT domains_users_pk PRIMARY KEY (user_id, domain_id),
    CONSTRAINT domains_users_f_user_id_fk
            foreign key (user_id) references oidc_users(id),
    CONSTRAINT domains_users_f_domain_id_fk
                foreign key (domain_id) references domains(id)
);

insert into domains_users (user_id, domain_id)
    select user_id, domain_id from (
    select distinct u.id user_id, d.id domain_id from oidc_users u, domains d)
    where NOT EXISTS (SELECT * FROM domains_users);

CREATE TABLE users_domains_roles
(
    user_id                     BIGINT                  NOT NULL,
    domain_id                   BIGINT                  NOT NULL,
    role_id                     BIGINT                  NOT NULL,
    create_on                   TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_domains_roles_pk PRIMARY KEY (user_id, domain_id, role_id),
    CONSTRAINT users_domains_roles_f_user_id_fk
            foreign key (user_id) references oidc_users(id),
    CONSTRAINT users_domains_roles_f_domain_id_fk
                foreign key (domain_id) references domains(id),
    CONSTRAINT users_domains_roles_f_role_id_fk
                foreign key (role_id) references roles(id)
);

insert into users_domains_roles (user_id, domain_id, role_id)
    select 1, 1, 1 union all
    select 1, 1, 4 union all
    select 2, 1, 2 union all
    select 2, 1, 4 union all
    select 3, 1, 4
    WHERE NOT EXISTS (SELECT * FROM users_domains_roles);

CREATE TABLE credential_types
(
    name                        VARCHAR(64)            PRIMARY KEY
);

insert into credential_types (name)
    select 'client'   union all
    select 'password'   union all
    select 'mobile_otp' union all
    select 'email_otp'  union all
    select 'two_factor' union all
    select 'social'
    WHERE NOT EXISTS (SELECT * FROM credential_types);

--CREATE TABLE client_group
--(
--    name                        VARCHAR(64)       NOT NULL PRIMARY KEY,
--    CONSTRAINT client_group_f_name_uk UNIQUE (name)
--);
--
--insert into client_group (name)
--    select 'system'   union all
--    select 'customer'   union all
--    select 'consumer'
--    WHERE NOT EXISTS (SELECT * FROM client_group);

CREATE TABLE domains_credential_type_order
(
    id                          BIGSERIAL           NOT NULL PRIMARY KEY,
    domain_id                   BIGINT              NOT NULL,
    credential_type             VARCHAR(64)         NOT NULL,
    parent_id                   BIGINT,
    captcha_enabled             BOOLEAN             NOT NULL DEFAULT FALSE,
    create_on                   TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT domains_credential_type_order_f_credential_type_fk
        foreign key (credential_type) references credential_types(name),
    CONSTRAINT domains_credential_type_order_f_domain_id_fk
        foreign key (domain_id) references domains(id)
);

insert into domains_credential_type_order (domain_id, credential_type, captcha_enabled)
    select id, 'password', true from domains
    WHERE NOT EXISTS (SELECT * FROM domains_credential_type_order);

CREATE TABLE client_credential
(
    client_id                   VARCHAR(50)             NOT NULL PRIMARY KEY,
    client_secret               VARCHAR(100)            NOT NULL,
    user_id                     BIGINT                  NOT NULL,
    create_on                   TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_on                   TIMESTAMP,
    CONSTRAINT client_credential_f_user_id_fk foreign key (user_id) references oidc_users(id)
);

--insert into client_credential (client_id, client_secret, user_id)
--    select 'XZlL4rRVv1', '$2a$10$kVgHb30lo9zTOHb1z8H5yuehNFq/Fx6D.30VXyIsKAYgqG1GH4.F6', id from oidc_users
--    WHERE NOT EXISTS (SELECT * FROM client_credential);

CREATE TABLE password_credential
(
    id                          BIGSERIAL               NOT NULL PRIMARY KEY,
    user_id                     BIGINT                  NOT NULL,
    should_be_change            BOOLEAN                 NOT NULL DEFAULT TRUE,
    password                    VARCHAR(128)            NOT NULL,
    create_on                   TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_on                   TIMESTAMP,
    CONSTRAINT password_credential_f_user_id_fk foreign key (user_id) references oidc_users(id)
);

insert into password_credential (user_id, password)
    select 1, '$2a$10$OQ0k4bUDoFi/SGe.5gi71eN7o8VGGFZ6LY2sPWdxSoKk6GwEuTaSW' union all
    select 2, '$2y$10$eUnFlltzrYMi7zvYHnfohOIRKCipcJMNQwmpGuvgzLGMMXOnNP31u' union all
    select 3, '$2y$10$tXcSaeWT7qgkPJGLfCNi4ukLJV1.fiuz.kb30TLV/naXYnSzF35.q'
    where NOT EXISTS (SELECT * FROM password_credential);

CREATE TABLE mobile_otp_credential
(
    id                          BIGSERIAL               NOT NULL PRIMARY KEY,
    user_id                     BIGINT                  NOT NULL,
    mobile                      CHAR(11)                NOT NULL,
    create_on                   TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_on                   TIMESTAMP,
    CONSTRAINT mobile_otp_credential_f_name_uk UNIQUE (mobile),
    CONSTRAINT mobile_otp_credential_f_user_id_fk foreign key (user_id) references oidc_users(id)
);

CREATE TABLE email_otp_credential
(
    id                          BIGSERIAL               NOT NULL PRIMARY KEY,
    user_id                     BIGINT                  NOT NULL,
    email                       VARCHAR(128)            NOT NULL,
    create_on                   TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_on                   TIMESTAMP,
    CONSTRAINT email_otp_credential_f_email_uk UNIQUE (email),
    CONSTRAINT email_otp_credential_f_user_id_fk foreign key (user_id) references oidc_users(id)
);

