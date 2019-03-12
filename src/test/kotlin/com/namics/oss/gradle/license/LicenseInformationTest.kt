package com.namics.oss.gradle.license

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * LicenseInformationTest.
 *
 * @author aschaefer, Namics AG
 * @since 2019-03-11 12:12
 */
class LicenseInformationTest {

    @Test
    fun license() {
        val systemUnderTest = LicenseDictionary()
        systemUnderTest.addConfig("---\n" +
                "id: Apache-2.0\n" +
                "category: Apache\n" +
                "name: Apache License 2.0\n" +
                "url: https://opensource.org/licenses/Apache-2.0\n" +
                "names:\n" +
                "  - Apache 2.0\n" +
                "  - Apache License 2.0\n" +
                "  - Apache License v2\n" +
                "  - Apache License, Version 2.0\n" +
                "  - Apache License, version 2.0\n" +
                "  - The Apache Software License\n" +
                "  - The Apache Software License, Version 2.0\n" +
                "  - The Apache License, Version 2.0\n" +
                "urls:\n" +
                "  - http://www.apache.org/licenses/LICENSE-2.0\n" +
                "  - http://www.apache.org/licenses/LICENSE-2.0.txt\n" +
                "  - https://www.apache.org/licenses/LICENSE-2.0\n" +
                "  - http://opensource.org/licenses/Apache-2.0\n" +
                "  - https://opensource.org/licenses/Apache-2.0\n" +
                "  - http://apache.org/licenses/LICENSE-2.0")
        val license = systemUnderTest.lookup("apache-2.0")!!
        assertEquals(license.id, "Apache-2.0")
        assertEquals(license.name, "Apache License 2.0")
        assertEquals(license.url, "https://opensource.org/licenses/Apache-2.0")
        assertEquals(license.category, "Apache")
    }
}
