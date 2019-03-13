
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.3.20"
    kotlin("jvm") version kotlinVersion
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "0.10.1"
    `java-gradle-plugin`
}

group = "com.namics.oss.gradle.license"
description = "Gradle plugin enforces licenses of dependencies to comply with definitions."
version = "1.0.2"

dependencies {
    compile("org.slf4j:slf4j-api:1.7.26")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.8")
    implementation("org.dom4j:dom4j:2.1.1")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.0")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.4.0")
}

repositories {
    mavenCentral()
}

tasks.withType(KotlinCompile::class) {
    kotlinOptions {
        javaParameters = true
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        register("com.namics.oss.gradle.license-enforce-plugin") {
            id = "com.namics.oss.gradle.license-enforce-plugin"
            implementationClass = "com.namics.oss.gradle.license.LicenseEnforcePlugin"
        }
    }
}

pluginBundle {
    website = "https://namics.github.io/license-enforce-plugin/"
    vcsUrl = "https://github.com/namics/license-enforce-plugin"
    description = project.description
    tags = listOf("dependency-management", "license", "enforce")
    (plugins) {
        "com.namics.oss.gradle.license-enforce-plugin" {
            // id is captured from java-gradle-plugin configuration
            displayName = "Gradle dependency licenses enforcement plugin"
            description = "Under development! Gradle dependency licenses enforcement plugin"
            tags = listOf("dependency-management", "license", "enforce")
            version = project.version.toString()
        }
    }
    mavenCoordinates {
        groupId = project.group.toString()
        artifactId = project.name
        version = project.version.toString()
    }
}



