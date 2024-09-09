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
