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


    @Test
    fun saxParserIssue() {
        val systemUnderTest = DependencyAnalyser(ProjectBuilder.builder().build(), emptyList())
        systemUnderTest.findLicenses(File(DependencyAnalyserTest::class.java.getResource("/xml/test.xml").file))
    }
}
