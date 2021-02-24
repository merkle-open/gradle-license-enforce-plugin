import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.namics.oss.gradle.license"
description = "Gradle plugin enforces licenses of dependencies to comply with definitions."

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.20"
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.12.0"
    `java-gradle-plugin`
    id("de.gliderpilot.semantic-release") version "1.4.0"
    id("com.github.hierynomus.license-base") version "0.15.0"
    id("com.github.ben-manes.versions") version "0.36.0"
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.0")
    implementation("org.dom4j:dom4j:2.1.3")
    implementation("xerces:xercesImpl:2.12.1")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
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

if (!version.toString().endsWith("-SNAPSHOT")){
    tasks.getByName("release"){
        finalizedBy("publishPlugins")
    }
}

tasks.create("licenseHeader"){
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
