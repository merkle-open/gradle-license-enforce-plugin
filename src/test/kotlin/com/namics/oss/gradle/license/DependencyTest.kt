/*
 * Copyright 2000-2019 Namics AG. All rights reserved.
 */

package com.namics.oss.gradle.license

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * DependencyTest.
 *
 * @author aschaefer, Namics AG
 * @since 2019-05-06 14:49
 */
class DependencyTest {
    @Test
    fun matchesWIthoutVersion() {
        assertTrue(Dependency("hello:world:1.2.3", emptyList()).matches(Dependency("hello:world:1.2.3", emptyList())))
        assertTrue(Dependency("hello:world:1.2.3", emptyList()).matches(Dependency("hello:world", emptyList())))
    }

    @Test
    fun matchesNoMatch() {
        assertFalse(Dependency("hello:world:1.2.3", emptyList()).matches(Dependency("hello:worlds:1.2.3", emptyList())))
        assertFalse(Dependency("hello:world:1.2.3", emptyList()).matches(Dependency("hello:1.2.3", emptyList())))
        assertFalse(Dependency("hello:world:1.2.3", emptyList()).matches(Dependency("", emptyList())))
    }

}
