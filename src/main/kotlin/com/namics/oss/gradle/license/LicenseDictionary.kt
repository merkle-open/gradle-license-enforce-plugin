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

import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor


class LicenseDictionary {

    private val byId: MutableMap<String, LicenseDefinition> = HashMap()
    private val byUrl: MutableMap<String, LicenseDefinition> = HashMap()
    private val byName: MutableMap<String, LicenseDefinition> = HashMap()

    fun addConfig(data: String) = Yaml(Constructor(LicenseDefinition::class.java, LoaderOptions())).loadAll(data).forEach {
        addDefinition(it as LicenseDefinition)
    }

    private fun addDefinition(input: LicenseDefinition) {
        val candidate = byId.getOrPut(input.id.lowercase()) { input }
        candidate.names.add(input.name)
        candidate.names.addAll(input.names)
        candidate.urls.add(input.url)
        candidate.urls.addAll(input.urls)

        byName[candidate.id.lowercase()] = candidate
        byName[candidate.name.lowercase()] = candidate
        candidate.names.forEach { byName[it.lowercase()] = candidate }

        byUrl[candidate.url.lowercase()] = candidate
        candidate.urls.forEach { byUrl[it.lowercase()] = candidate }
    }

    fun lookup(representation: String) = representation.lowercase().let { byUrl[it] ?: byName[it] }

    fun knownLicenses() = byId.values
}
