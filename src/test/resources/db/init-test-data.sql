CREATE SEQUENCE IF NOT EXISTS link_seq
    START WITH 42
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

DELETE FROM link;
DELETE FROM request;

INSERT INTO request (source, blocked_until, count)
  VALUES ('1.1.1.1', '2200-01-01', 1);
