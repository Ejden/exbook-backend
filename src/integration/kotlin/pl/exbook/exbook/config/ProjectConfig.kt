package pl.exbook.exbook.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringExtension
import org.springframework.test.context.DynamicPropertyRegistry
import pl.exbook.exbook.config.Constants.DATABASE_NAME

object ProjectConfig : AbstractProjectConfig() {
    private val mongoListener = MongoListener()

    override fun extensions() = listOf(
        SpringExtension,
        mongoListener
    )

    fun updateConfiguration(registry: DynamicPropertyRegistry) {
        registry.add("spring.data.mongodb.uri") { mongoListener.replicaUrl() }
        registry.add("spring.data.mongodb.database") { DATABASE_NAME }
    }
}
