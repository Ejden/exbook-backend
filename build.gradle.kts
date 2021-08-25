import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("groovy")
    id("org.springframework.boot") version "2.4.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("io.swagger.core.v3.swagger-gradle-plugin") version "2.1.6"
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.spring") version "1.4.21"
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
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.github.microutils:kotlin-logging:1.12.0")
    implementation("io.springfox:springfox-swagger2:3.0.0")
    implementation("io.springfox:springfox-boot-starter:3.0.0")
    implementation("io.springfox:springfox-swagger-ui:3.0.0")
    implementation("com.auth0:java-jwt:3.8.3")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.spockframework:spock-core:2.0-groovy-2.5")
    testImplementation("org.codehaus.groovy:groovy-all:3.0.8")

    integrationImplementation("org.springframework.security:spring-security-test")
    integrationImplementation("org.springframework.boot:spring-boot-starter-test")
    integrationImplementation("org.spockframework:spock-core:2.0-groovy-2.5")
    integrationImplementation("org.spockframework:spock-spring:2.0-groovy-2.5")
    integrationImplementation("org.codehaus.groovy:groovy-all:3.0.8")
    integrationImplementation("org.springframework:spring-test:5.3.9")
    integrationImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
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
    java.srcDir("src/integration/groovy")
    resources.srcDir("src/integration/resources")
}

tasks.create<Test>("integration") {
    testClassesDirs = sourceSets["integration"].output.classesDirs
    classpath = sourceSets["integration"].runtimeClasspath
}
