load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

RULES_JVM_EXTERNAL_TAG = "5.3"

RULES_JVM_EXTERNAL_SHA = "d31e369b854322ca5098ea12c69d7175ded971435e55c18dd9dd5f29cc5249ac"

http_archive(
    name = "rules_jvm_external",
    sha256 = RULES_JVM_EXTERNAL_SHA,
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    url = "https://github.com/bazelbuild/rules_jvm_external/releases/download/%s/rules_jvm_external-%s.tar.gz" % (RULES_JVM_EXTERNAL_TAG, RULES_JVM_EXTERNAL_TAG),
)

load("@rules_jvm_external//:repositories.bzl", "rules_jvm_external_deps")

rules_jvm_external_deps()

load("@rules_jvm_external//:setup.bzl", "rules_jvm_external_setup")

rules_jvm_external_setup()

load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = [
        "com.fasterxml.jackson.core:jackson-databind:2.15.2",
        "com.fasterxml.jackson.core:jackson-annotations:2.15.2",
        "com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2",
        "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2",
        "io.javalin:javalin:5.6.1",
        "org.slf4j:slf4j-simple:2.0.9",
        "junit:junit:4.13.2",
        "org.assertj:assertj-core:3.20.2",
        "org.webjars.npm:axios:jar:1.5.0",
        "org.webjars.npm:vue:jar:3.3.4",
        "com.wgtwo.api.v1.grpc:sms:1.10.1",
        "com.wgtwo.api.v0.grpc:events:0.1.6",
        "com.wgtwo.api:auth:0.0.3",
        "com.googlecode.libphonenumber:libphonenumber:8.13.14",
        "com.google.protobuf:protobuf-java-util:3.18.1",
        "com.sksamuel.hoplite:hoplite-core:2.7.5",
        "com.sksamuel.hoplite:hoplite-toml:2.7.5",
    ],
    repositories = [
        "https://maven.google.com",
        "https://repo1.maven.org/maven2",
    ],
)

rules_kotlin_version = "1.8"

rules_kotlin_sha = "01293740a16e474669aba5b5a1fe3d368de5832442f164e4fbfc566815a8bc3a"

http_archive(
    name = "rules_kotlin",
    sha256 = rules_kotlin_sha,
    urls = ["https://github.com/bazelbuild/rules_kotlin/releases/download/v%s/rules_kotlin_release.tgz" % rules_kotlin_version],
)

load("@rules_kotlin//kotlin:repositories.bzl", "kotlin_repositories")

kotlin_repositories()

register_toolchains("//:kotlin_toolchain")
