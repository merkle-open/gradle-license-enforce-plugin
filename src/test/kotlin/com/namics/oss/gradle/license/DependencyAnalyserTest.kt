/*
 * Copyright 2000-2020 Namics AG. All rights reserved.
 */

package com.namics.oss.gradle.license

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import java.io.File

/**
 * DependencyAnalyserTest.
 *
 * @author aschaefer, Namics AG
 * @since 29.06.20 10:57
 */
internal class DependencyAnalyserTest {

    val systemUnderTest = DependencyAnalyser(ProjectBuilder.builder().build(), emptyList())

    @Test
    fun apache() {
        systemUnderTest.findLicenses(File(DependencyAnalyserTest::class.java.getResource("/xml/apache.pom").file))
    }

    @Test
    fun logbackParent() {
        systemUnderTest.findLicenses(File(DependencyAnalyserTest::class.java.getResource("/xml/logback-parent.pom").file))
    }

}
