package com.brevphoenix

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
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

//        config.plugins.enableRouteOverview("/routes")
    }

    app.routes {
        get("/", VueComponent("page-welcome"))
    }

    app.start(appConfig.port)
}
