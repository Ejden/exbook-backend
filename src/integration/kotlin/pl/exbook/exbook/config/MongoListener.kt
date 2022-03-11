package pl.exbook.exbook.config

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import io.kotest.core.listeners.AfterEachListener
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.wait.strategy.Wait

class MongoListener : BeforeProjectListener, AfterProjectListener, AfterTestListener {
    lateinit var container: MongoDBContainer
    lateinit var client: MongoClient

    fun replicaUrl(): String = container.replicaSetUrl

    override suspend fun beforeProject() {
        container = MongoDBContainer(Constants.MONGODB_IMAGE)
        container.start()
        container.waitingFor(
            Wait.forListeningPort().withStartupTimeout(Constants.CONTAINER_STARTUP_TIMEOUT)
        )
        client = MongoClients.create(replicaUrl())
    }

    override suspend fun afterProject() {
        client.close()
        container.close()
    }

    override suspend fun afterAny(testCase: TestCase, result: TestResult) {
        with(client.getDatabase(Constants.DATABASE_NAME)) {
            listCollectionNames().forEach {
                getCollection(it).drop()
            }
        }
    }
}
