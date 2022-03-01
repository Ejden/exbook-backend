package pl.exbook.exbook

import io.kotest.core.spec.style.ShouldSpec
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import pl.exbook.exbook.config.ProjectConfig

@SpringBootTest(classes = [AppRunner::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureDataMongo
@AutoConfigureMockMvc
abstract class BaseIntegrationSpec(body: ShouldSpec.() -> Unit) : ShouldSpec(body) {
    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun injectContainerData(registry: DynamicPropertyRegistry) {
            ProjectConfig.updateConfiguration(registry)
        }
    }
}
