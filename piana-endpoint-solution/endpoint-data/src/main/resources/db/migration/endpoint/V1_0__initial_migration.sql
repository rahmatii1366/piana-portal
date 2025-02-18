DROP TABLE IF EXISTS shedlock CASCADE;
DROP TABLE IF EXISTS endpoints CASCADE;
DROP TABLE IF EXISTS endpoint_limitation CASCADE;

DROP SEQUENCE IF EXISTS serial_sequence;

CREATE SEQUENCE serial_sequence
    INCREMENT 1
START 10000;

CREATE TABLE shedlock
(
    name       VARCHAR(64)  NOT NULL,
    lock_until TIMESTAMP    NOT NULL,
    locked_at  TIMESTAMP    NOT NULL,
    locked_by  VARCHAR(255) NOT NULL,
    PRIMARY KEY (name)
);

CREATE TABLE service_point (
    id                          BIGSERIAL       NOT NULL PRIMARY KEY,
    name                        varchar(64)     NOT NULL,
    description                 varchar(256),
    disabled                    BOOLEAN         NOT NULL default true,
    create_on                   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT service_point_f_name_create_on_uk UNIQUE (name, create_on)
);

ALTER SEQUENCE "service_point_id_seq" RESTART WITH 100;

insert into service_point (id, name, description)
    select 1, 'vehicle_third_party_insurance', ''
    WHERE NOT EXISTS (SELECT * FROM service_point);

CREATE TABLE endpoint (
    id                          BIGSERIAL       NOT NULL PRIMARY KEY,
    service_point_id            BIGINT          NOT NULL,
    execution_order             INT             NOT NULL,
    name                        varchar(64)     NOT NULL,
    is_debug_mode               BOOLEAN         NOT NULL default false,
    is_secure                   BOOLEAN         NOT NULL default false,
    host                        varchar(64)     NOT NULL,
    port                        INT             NOT NULL,
    base_url                    VARCHAR(64)     NOT NULL default '',
    so_timeout                  INT             NOT NULL default 60,
    connection_timeout          INT             NOT NULL default 60,
    socket_timeout              INT             NOT NULL default 60,
    time_to_live                INT             NOT NULL default 600,
    pool_reuse_policy           varchar(10)     NOT NULL default 'LIFO',
    pool_concurrency_policy     varchar(10)     NOT NULL default 'STRICT',
    trust_store                 varchar(256),
    trust_store_password        varchar(256),
    tls_versions                varchar(64)     NOT NULL default 'V_1_3',
    disabled                    BOOLEAN         NOT NULL default true,
    create_on                   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT endpoint_f_service_point_id_execution_order_create_on_uk UNIQUE (service_point_id, execution_order, create_on),
    CONSTRAINT endpoint_f_service_point_id_name_create_on_uk UNIQUE (service_point_id, name, create_on),
    CONSTRAINT endpoint_f_service_point_id_fk foreign key(service_point_id) REFERENCES service_point (id)
);

ALTER SEQUENCE "endpoint_id_seq" RESTART WITH 100;

insert into endpoint (id, service_point_id, execution_order, name, is_secure, host, port, base_url,
    so_timeout, connection_timeout, socket_timeout, time_to_live,
    pool_reuse_policy, pool_concurrency_policy,
     trust_store, trust_store_password, tls_versions)
    select 1, 1, 1, 'taban', true, 'sw.tabanapisis.org', 9000, '',
        30, 30, 30, 120,
        'LIFO', 'STRICT',
        '', '', 'V_1_3,V_1_2'
    WHERE NOT EXISTS (SELECT * FROM endpoint);

CREATE TABLE endpoint_limitation (
    id                          BIGSERIAL       NOT NULL PRIMARY KEY,
    service_point_id            BIGINT     NOT NULL,
    endpoint_name               varchar(64)     NOT NULL,
    start_at                    CHAR(10)        NOT NULL,
    expire_at                   CHAR(10)        NOT NULL,
    limitation_in_tps           INT,
    limitation_in_minute        INT,
    limitation_in_hour          INT,
    limitation_in_day           INT,
    limitation_in_week          INT,
    limitation_in_month         INT,
    limitation_in_year          INT,
    limitation_in_period        INT,
    create_on                   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT endpoint_limitation_f_service_point_id_fk foreign key (service_point_id) REFERENCES service_point (id),
    CONSTRAINT endpoint_limitation_f_service_point_id_endpoint_name_create_on_uk UNIQUE(service_point_id, endpoint_name, create_on)
);

ALTER SEQUENCE "endpoint_limitation_id_seq" RESTART WITH 100;

insert into endpoint_limitation (id, service_point_id, endpoint_name, start_at, expire_at,
    limitation_in_tps, limitation_in_day, limitation_in_period)
    select 1, 1, 'taban', '1403/11/16', '1403/12/29', 5, 1000, 15000
    WHERE NOT EXISTS (SELECT * FROM endpoint_limitation);

CREATE TABLE endpoint_call_log (
    id                          BIGSERIAL       NOT NULL PRIMARY KEY,
    service_point_id            BIGINT          NOT NULL,
    endpoint_id                 BIGINT          NOT NULL,
    reference_id                VARCHAR(32)     NOT NULL,
    merchant_id                 BIGINT,
    requester_id                BIGINT,
    before_call                 BOOLEAN         NOT NULL DEFAULT false,
    response_status_code        INT,
    response_body               INT,
    create_on                   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);