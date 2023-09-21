package com.brevphoenix

import com.brevphoenix.auth.AccessManager
import com.brevphoenix.auth.Role
import com.brevphoenix.auth.currentUser
import com.brevphoenix.signin.SigninHandler
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.http.staticfiles.Location
import io.javalin.vue.VueComponent
import org.eclipse.jetty.server.handler.ContextHandler.ApproveAliases

val appConfig: Config = Config()

fun main() {

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
    }

    app.routes {
        get("/sign-in", VueComponent("page-sign-in"), Role.ANY)
        get("/", VueComponent("page-welcome"), Role.SIGNED_IN)

        path("/api") {
            path("/auth") {
                post("/send-pin", SigninHandler::sendPin, Role.ANY)
                post("/validate-pin", SigninHandler::validatePin, Role.ANY)
            }
        }
    }

    app.start(appConfig.port)

    Runtime.getRuntime().addShutdownHook(
        Thread {
            app.stop()
            GrpcShared.close()
        },
    )
}
