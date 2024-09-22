CREATE DATABASE brevphoenix;

\connect brevphoenix;

CREATE ROLE brevphoenix WITH LOGIN PASSWORD 'password' NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT;

GRANT CONNECT ON DATABASE brevphoenix TO brevphoenix;
GRANT ALL PRIVILEGES ON DATABASE brevphoenix to brevphoenix;

CREATE SCHEMA brevphoenix_schema AUTHORIZATION brevphoenix;
ALTER ROLE brevphoenix SET search_path TO brevphoenix_schema;
ALTER DATABASE brevphoenix SET search_path TO brevphoenix_schema;

\connect brevphoenix brevphoenix;

CREATE EXTENSION pgcrypto;

\connect brevphoenix postgres;
CREATE DATABASE brevphoenix_session;
\connect brevphoenix_session postgres;

CREATE ROLE brevphoenix_session WITH LOGIN PASSWORD 'password' NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT;

GRANT CONNECT ON DATABASE brevphoenix_session TO brevphoenix_session;
GRANT ALL PRIVILEGES ON DATABASE brevphoenix_session to brevphoenix_session;

CREATE SCHEMA brevphoenix_session_schema AUTHORIZATION brevphoenix_session;
ALTER ROLE brevphoenix_session SET search_path TO brevphoenix_session_schema;
ALTER DATABASE brevphoenix_session SET search_path TO brevphoenix_session_schema;
