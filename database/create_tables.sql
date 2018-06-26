CREATE TABLE link (
    id bigint PRIMARY KEY,
    created timestamp without time zone DEFAULT 'now',
    updated timestamp without time zone DEFAULT 'now',
    expired timestamp without time zone,
    url text,
    source character varying(100),
    permanent boolean NOT NULL DEFAULT TRUE,
    flagged boolean NOT NULL DEFAULT FALSE,
    test boolean NOT NULL DEFAULT FALSE
);

CREATE SEQUENCE link_seq
    START WITH 42
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE request (
    source character varying(100) PRIMARY KEY,
    last_time timestamp without time zone DEFAULT 'now',
    blocked_until timestamp without time zone,
    count int DEFAULT 1
);
