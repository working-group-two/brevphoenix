# Starter template for Kotlin Javalin in Bazel

## Set up database

```bash
# start a docker postgres container
docker run --name postgres -e POSTGRES_PASSWORD=postgres -d -p 5432:5432 postgres
# run the sql init script to create the database
psql -h localhost -U postgres -f src/main/resources/sql/init.sql
```

## Start the server

```bash
echo "\
[oAuth]
clientId = \"3c23…\"
clientSecret = \"4ACd…\"" > config/credentials.toml
vim config/credentials.toml # add your https://developer.mobility.cisco.com/ OAuth credentials here
bazel run //:localhost
```

## Intellisense

To get intellisense for Tailwind in your IDE, run the following command (the files needs to be available in a
node_modules/worksspace folder):

```bash
pnpm install # or npm install
```
