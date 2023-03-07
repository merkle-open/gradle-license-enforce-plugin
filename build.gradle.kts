group = "com.namics.oss.gradle.license"
description = "Gradle plugin enforces licenses of dependencies to comply with definitions."

plugins {
    kotlin("jvm") version embeddedKotlinVersion
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.1.0"
    `java-gradle-plugin`
    id("fr.brouillard.oss.gradle.jgitver") version "0.10.0-rc03"
    id("com.github.hierynomus.license-base") version "0.16.1"
    id("com.github.ben-manes.versions") version "0.46.0"
}

dependencies {
    implementation(platform(kotlin("bom")))

    implementation("org.dom4j:dom4j:2.1.4")
    implementation("org.yaml:snakeyaml:2.0")
    implementation("xerces:xercesImpl:2.12.2")

    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

repositories {
    mavenCentral()
}
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

gradlePlugin {
    website.set("https://github.com/merkle-open/gradle-license-enforce-plugin")
    vcsUrl.set("https://github.com/merkle-open/gradle-license-enforce-plugin")
    plugins {
        create("com.namics.oss.gradle.license-enforce-plugin") {
            id = "com.namics.oss.gradle.license-enforce-plugin"
            displayName = "Gradle dependency licenses enforcement plugin"
            description = "Under development! ${project.description}"
            tags.set(listOf("dependency-management", "license", "enforce"))
            implementationClass = "com.namics.oss.gradle.license.LicenseEnforcePlugin"
        }
    }
}

jgitver {
    useDistance = false
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
