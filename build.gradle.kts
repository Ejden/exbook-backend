import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val versions: Map<String, String> = mapOf(

)

plugins {
    id("org.springframework.boot") version "2.4.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("io.swagger.core.v3.swagger-gradle-plugin") version "2.1.6"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
}

group = "pl.exbook"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

val integrationCompile: Configuration by configurations.creating {
    extendsFrom(configurations.testCompile.get())
}

val integrationRuntime: Configuration by configurations.creating {
    extendsFrom(configurations.testRuntime.get())
}

val integrationImplementation: Configuration by configurations.creating {
    extendsFrom(configurations.testImplementation.get())
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb:2.5.5")
    implementation("org.springframework.boot:spring-boot-starter-web:2.5.5")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-security:2.5.5")
    implementation("io.github.microutils:kotlin-logging:2.0.11")
    implementation("io.springfox:springfox-swagger2:3.0.0")
    implementation("io.springfox:springfox-boot-starter:3.0.0")
    implementation("io.springfox:springfox-swagger-ui:3.0.0")
    implementation("com.auth0:java-jwt:3.18.2")
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
    testImplementation("io.mockk:mockk:1.12.2")
    testImplementation("io.kotest:kotest-runner-junit5:5.1.0")
    testImplementation("io.kotest:kotest-assertions-core:5.1.0")

    integrationImplementation("org.springframework.security:spring-security-test")
    integrationImplementation("org.springframework.boot:spring-boot-starter-test")
    integrationImplementation("org.springframework:spring-test:5.3.9")
    integrationImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
}

tasks.wrapper {
    gradleVersion = "7.3.3"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

sourceSets.create("integration") {
    compileClasspath += project.sourceSets["main"].output + project.sourceSets["test"].output
    runtimeClasspath += project.sourceSets["main"].output + project.sourceSets["test"].output
    java.srcDir("src/integration/kotlin")
    resources.srcDir("src/integration/resources")
}

tasks.create<Test>("integration") {
    testClassesDirs = sourceSets["integration"].output.classesDirs
    classpath = sourceSets["integration"].runtimeClasspath
    mustRunAfter("test")
}

tasks {
    check {
        dependsOn("integration")
    }
}
