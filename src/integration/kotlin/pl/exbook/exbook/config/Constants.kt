package pl.exbook.exbook.config

import java.time.Duration

object Constants {
    const val WIREMOCK_SERVER_PORT = 9999
    const val MONGODB_IMAGE = "mongo:4.0.10"
    val CONTAINER_STARTUP_TIMEOUT = Duration.ofSeconds(180L)
    const val DATABASE_NAME = "database"
}
