# Starter template for Kotlin Javalin in Bazel

## Add public/private key pair for encryption of SMS

```bash
gpg --full-generate-key # generate a key pair, choose RSA+RSA to avoid pgp decryption issues
gpg --armor --export <KEY_ID> > ./config/public_key.txt
gpg --armor --export-secret-keys <KEY_ID> > ./config/private_key.txt
```

## Set up database

```bash
# start a docker postgres container
docker run --name postgres -e POSTGRES_PASSWORD=postgres -d -p 5432:5432 postgres
# run the sql init script to create the database
psql -h localhost -U postgres -f src/main/resources/sql/init.sql # password is 'postgres' for user postgres, 'password' for brevphoenix
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
