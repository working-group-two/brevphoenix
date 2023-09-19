load("@rules_java//java:defs.bzl", "java_binary", "java_library", "java_test")
load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

package(default_visibility = ["//visibility:public"])

java_binary(
    name = "server",
    data = [
    ],
    main_class = "com.brevphoenix.MainKt",
    runtime_deps = [
        ":server_lib",
    ],
)

kt_jvm_library(
    name = "server_lib",
    srcs = glob(
        include = [
            "src/main/kotlin/**/*.kt",
        ],
    ),
    data = [
        ":data",
    ],
    deps = [
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:com_fasterxml_jackson_module_jackson_module_kotlin",
        "@maven//:io_javalin_javalin",
        "@maven//:org_slf4j_slf4j_simple",
        "@maven//:org_webjars_npm_axios",
        "@maven//:org_webjars_npm_vue",
    ],
)

java_library(
    name = "java_test_deps",
    testonly = True,
    exports = [
        "@maven//:junit_junit",
        "@maven//:org_assertj_assertj_core",
    ],
)

kt_jvm_library(
    name = "kotlin_test_deps",
    testonly = True,
    srcs = glob(["src/test/kotlin/**/*.kt"]),
    deps = [
        ":java_test_deps",
    ],
)

java_test(
    name = "tests",
    test_class = "com.brevphoenix.HelloTest",
    runtime_deps = [
        ":kotlin_test_deps",
    ],
)

filegroup(
    name = "data",
    srcs =
        glob([
            "src/main/resources/public/**/*",
            #            "src/main/resources/vue/**/*",
            "src/main/resources/vue/layout.html",
            "src/main/resources/vue/page-welcome.vue",
        ]),
)
