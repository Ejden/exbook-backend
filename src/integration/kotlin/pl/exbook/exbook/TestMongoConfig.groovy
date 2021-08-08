package pl.exbook.exbook

import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodProcess
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.stereotype.Component

@Component
class TestMongoConfig {

    private final MongoDatabaseFactory mongo

    TestMongoConfig(MongoDatabaseFactory mongo) {
        this.mongo = mongo
    }

//    @Autowired
//    private MongoProperties properties;
//
//    @Bean
//    Mongo mongo(MongodProcess mongodProcess) throws IOException {
//        Net net = mongodProcess.getConfig().net();
//        properties.setHost(net.getServerAddress().getHostName());
//        properties.setPort(net.getPort());
//        return properties.createMongoClient(this.options);
//    }
//
//    @Bean(destroyMethod = "stop")
//    MongodProcess mongodProcess(MongodExecutable mongodExecutable) throws IOException {
//        return mongodExecutable.start();
//    }
//
//    @Bean(destroyMethod = "stop")
//    MongodExecutable mongodExecutable(MongodStarter mongodStarter, IMongodConfig iMongodConfig) throws IOException {
//        return mongodStarter.prepare(iMongodConfig);
//    }
//
//    @Bean
//    IMongodConfig mongodConfig() throws IOException {
//        return new MongodConfigBuilder().version(Version.Main.PRODUCTION).build();
//    }
//
//    @Bean
//    MongodStarter mongodStarter() {
//        return MongodStarter.getDefaultInstance();
//    }
}
