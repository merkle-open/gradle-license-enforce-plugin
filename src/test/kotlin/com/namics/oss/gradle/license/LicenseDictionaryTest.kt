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

import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.test.assertEquals
import kotlin.test.Test

/**
 * @author aschaefer, Namics AG
 * @since 2019-03-11 12:12
 */
class LicenseDictionaryTest {

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


    @Test
    fun bundled() {
        val systemUnderTest = LicenseDictionary()
        val resource = BufferedReader(InputStreamReader(LicenseDictionary::class.java.getResourceAsStream("license-dictionary.yaml")!!)).readText()
        systemUnderTest.addConfig(resource)
    }
}
