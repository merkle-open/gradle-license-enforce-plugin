/**
 * MIT License
 *
 * Copyright (c) 2019 Namics AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.namics.oss.gradle.license

import org.dom4j.Element
import org.dom4j.io.SAXReader
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.kotlin.dsl.get
import java.io.File

public class DependencyAnalyser(val project: Project,
                                val analyseConfigurations: List<String>) {

    fun analyse(): List<Dependency> {
        setupEnvironment()
        collectDependencies()
        return dependencyInformation()
    }

    /**
     * Setup configurations to collect dependencies.
     */
    private fun setupEnvironment() {
        // Create temporary configuration in order to store POM information
        project.getConfigurations().create(POM_CONFIGURATION)
        project.getConfigurations().forEach {
            try {
                it.isCanBeResolved = true
            } catch (ignore: Exception) {
                project.logger.info("cannot resolve configuration {}", it, ignore)
            }
        }
    }


    /**
     * Iterate through all configurations and collect dependencies.
     */
    private fun collectDependencies() {
        // Add POM information to our POM configuration
        val configurations = LinkedHashSet<Configuration>()

        project.getConfigurations()
                .filter { analyseConfigurations.contains(it.getName()) }
                .forEach { configurations.add(it) }

        configurations
                .filter { it.isCanBeResolved }
                .map { it.resolvedConfiguration }
                .map { it.lenientConfiguration }
                .flatMap { it.artifacts }
                .map { "${it.moduleVersion.id.group}:${it.moduleVersion.id.name}:${it.moduleVersion.id.version}@pom" }
                .forEach { project.configurations.getByName(POM_CONFIGURATION).dependencies.add(project.dependencies.add(POM_CONFIGURATION, it)) }
    }

    private fun dependencyInformation(): List<Dependency> {
        val artifacts = project.getConfigurations().getByName(POM_CONFIGURATION).resolvedConfiguration.lenientConfiguration.artifacts
        return artifacts.map {
            val pom = it
            val file = pom.file
            val coordinates = pom.id.componentIdentifier.displayName
            val licenses = findLicenses(file)
            Dependency(coordinates, licenses)
        }.sortedBy { it.id }
    }


    fun findLicenses(pomFile: File): List<License> {
        try {

            val reader = SAXReader(false)
            reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
            reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            reader.setFeature("http://xml.org/sax/features/namespaces", false);
            reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
            val doc = reader.read(pomFile)

            if (ANDROID_SUPPORT_GROUP_ID == doc?.rootElement?.element("group")?.text) {
                return listOf(License(name = APACHE_LICENSE_NAME, url = APACHE_LICENSE_URL))
            }

            val lics = doc?.rootElement?.element("licenses")?.elements("license")
                    ?.filterNotNull()
                    ?.map { License(it.element("name")?.text ?: "", it.element("url")?.text ?: "") }

            if (lics != null)
                return lics

            val parent = doc.rootElement.element("parent")
            if (parent != null)
                return findLicenses(getParentPomFile(parent))

        } catch (e: Throwable) {
            project.logger.warn("Failed to analyse {}", pomFile, e)
        }
        return emptyList()
    }

    /**
     * Use Parent POM information when individual dependency license information is missing.
     */
    private fun getParentPomFile(parent: Element): File {
        val groupId = parent.element("groupId").text
        val artifactId = parent.element("artifactId").text
        val version = parent.element("version").text
        val dependency = "$groupId:$artifactId:$version@pom"

        // Add dependency to temporary configuration
        project.getConfigurations().create(TEMP_POM_CONFIGURATION)

        project.getConfigurations().get(TEMP_POM_CONFIGURATION).dependencies.add(
                project.getDependencies().add(TEMP_POM_CONFIGURATION, dependency)
        )

        // resolve file
        val file = project.getConfigurations().get(TEMP_POM_CONFIGURATION).resolvedConfiguration.lenientConfiguration.artifacts.iterator().next().file

        // cleanup
        project.getConfigurations().remove(project.getConfigurations().get(TEMP_POM_CONFIGURATION))

        return file
    }


    companion object {
        private val ANDROID_SUPPORT_GROUP_ID = "com.android.support"
        private val APACHE_LICENSE_NAME = "Apache License 2.0"
        private val APACHE_LICENSE_URL = "https://www.apache.org/licenses/LICENSE-2.0.txt"

        private val POM_CONFIGURATION = "dependencyAnalyserPoms"
        private val TEMP_POM_CONFIGURATION = "dependencyAnalyserPomsTemp"
    }
}
