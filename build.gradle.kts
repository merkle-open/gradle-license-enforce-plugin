group = "com.namics.oss.gradle.license"
description = "Gradle plugin enforces licenses of dependencies to comply with definitions."

plugins {
    kotlin("jvm") version embeddedKotlinVersion
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.17.0"
    `java-gradle-plugin`
    id("fr.brouillard.oss.gradle.jgitver") version "0.10.0-rc03"
    id("com.github.hierynomus.license-base") version "0.16.1"
    id("com.github.ben-manes.versions") version "0.39.0"
}

dependencies {
    implementation(platform(kotlin("bom")))

    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.0")
    implementation("org.dom4j:dom4j:2.1.3")
    implementation("xerces:xercesImpl:2.12.1")

    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
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

jgitver {
    useDistance = false
}

pluginBundle {
    website = "https://github.com/namics/gradle-license-enforce-plugin"
    vcsUrl = "https://github.com/namics/gradle-license-enforce-plugin"
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

tasks.create("licenseHeader") {
    dependsOn("licenseFormatMain", "licenseFormatTest")
}

tasks.named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates") {
    resolutionStrategy {
        componentSelection {
            all {
                val rc = listOf("alpha", "beta", "rc", "cr", "m", "preview", "b", "ea", "pr").any { qualifier ->
                    candidate.version.matches(Regex("(?i).*[.-]$qualifier[.\\d-+]*"))
                }

                if (rc) {
                    reject("Release candidate")
                }
            }
        }
    }
    checkForGradleUpdate = true
    revision = "release"
}
