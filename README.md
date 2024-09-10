# Starter template for Kotlin Javalin in Bazel

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
