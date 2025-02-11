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

CREATE TABLE endpoint (
    id                          BIGSERIAL       NOT NULL PRIMARY KEY,
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
    update_on                   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT endpoint_f_name_uk UNIQUE (name)
);

ALTER SEQUENCE "endpoint_id_seq" RESTART WITH 100;

insert into endpoint (id, name, is_secure, host, port, base_url,
    so_timeout, connection_timeout, socket_timeout, time_to_live,
    pool_reuse_policy, pool_concurrency_policy,
     trust_store, trust_store_password, tls_versions)
    select 1, 'taban', true, 'sw.tabanapisis.org', 9000, '',
        30, 30, 30, 120,
        'LIFO', 'STRICT',
        '', '', 'V_1_3,V_1_2'
    WHERE NOT EXISTS (SELECT * FROM endpoint);

CREATE TABLE endpoint_limitation
(
    id                          BIGSERIAL       NOT NULL PRIMARY KEY,
    endpoint_id                 BIGINT          NOT NULL,
    start_date                  CHAR(10)        NOT NULL,
    expire_at                   CHAR(10)        NOT NULL,
    limitation_in_day           INT             NOT NULL,
    limitation_in_period        INT             NOT NULL,
    create_on                   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER SEQUENCE "endpoint_limitation_id_seq" RESTART WITH 100;

insert into endpoint_limitation (id, endpoint_id, start_date, expire_at, limitation_in_day, limitation_in_period)
    select 1, 1, '1403/11/16', '1403/12/29', 100, 1500
    WHERE NOT EXISTS (SELECT * FROM endpoint_limitation);