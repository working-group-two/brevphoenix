CREATE ROLE brevphoenix WITH LOGIN PASSWORD 'password' NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT;

CREATE DATABASE brevphoenix;
GRANT CONNECT ON DATABASE brevphoenix TO brevphoenix;
GRANT ALL PRIVILEGES ON DATABASE brevphoenix to brevphoenix;

CREATE DATABASE brevphoenix_session;
GRANT CONNECT ON DATABASE brevphoenix_session TO brevphoenix;
GRANT ALL PRIVILEGES ON DATABASE brevphoenix_session to brevphoenix;
