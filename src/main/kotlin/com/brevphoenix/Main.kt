package com.brevphoenix

import com.brevphoenix.auth.AccessManager
import com.brevphoenix.auth.Role
import com.brevphoenix.auth.currentUser
import com.brevphoenix.signin.SigninHandler
import com.brevphoenix.sms.SmsController
import com.brevphoenix.sms.SmsDatabaseHandler
import com.brevphoenix.sms.SmsService
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.sksamuel.hoplite.ConfigLoaderBuilder
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.apibuilder.ApiBuilder.ws
import io.javalin.http.staticfiles.Location
import io.javalin.json.JavalinJackson
import io.javalin.validation.JavalinValidation
import io.javalin.vue.VueComponent
import org.eclipse.jetty.server.handler.ContextHandler.ApproveAliases
import java.io.File
import com.sksamuel.hoplite.PropertySource
import com.sksamuel.hoplite.sources.EnvironmentVariablesPropertySource

lateinit var config: Config

fun main(args: Array<String>) {
    config = ConfigLoaderBuilder.default()
        .addPropertySource(
            EnvironmentVariablesPropertySource(
                useUnderscoresAsSeparator = true,
                allowUppercaseNames = true,
            ),
        )
        .addPropertySources(args.reversed().map(::toPropertySource))
        .build()
        .loadConfigOrThrow()


    SmsDatabaseHandler.init()

    val app = Javalin.create { config ->
        config.staticFiles.add {
            it.directory = "src/main/resources/public"
            it.location = Location.EXTERNAL
            it.aliasCheck = ApproveAliases()
        }

        config.staticFiles.enableWebjars()
        config.vue.rootDirectory(path = "src/main/resources/vue", Location.EXTERNAL)
        config.vue.vueAppName = "app"

        config.accessManager(AccessManager::manage)
        config.jetty.sessionHandler(AccessManager::sessionHandler)
        config.vue.stateFunction = { ctx -> mapOf("currentUser" to ctx.currentUser) }

        config.jsonMapper(
            JavalinJackson(
                jacksonObjectMapper()
                    .registerModule(JavaTimeModule())
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .enable(SerializationFeature.INDENT_OUTPUT)
            )

        )
    }

    JavalinValidation.register(PhoneNumber::class.java, PhoneNumber::parse)
    SmsService.init()

    app.routes {
        get("/sign-in", VueComponent("page-sign-in"), Role.ANY)
        get("/", VueComponent("page-welcome"), Role.SIGNED_IN)

        path("/api") {
            path("/auth") {
                post("/send-pin", SigninHandler::sendPin, Role.ANY)
                post("/validate-pin", SigninHandler::validatePin, Role.ANY)
            }
            path("/sms") {
                get("/", SmsController::getSms, Role.SIGNED_IN)
                post("/{to}", SmsController::sendSms, Role.SIGNED_IN)
                ws("/", { ws ->
                    ws.onConnect(SmsController::handleWsConnect)
                    ws.onClose(SmsController::handleWsClose)
                    ws.onError(SmsController::handleWsError)
                }, Role.SIGNED_IN)
            }
        }
    }

    app.start(config.port)

    Runtime.getRuntime().addShutdownHook(
        Thread {
            app.stop()
            GrpcShared.close()
        },
    )
}

private fun toPropertySource(file: String) = PropertySource.file(File(file))
