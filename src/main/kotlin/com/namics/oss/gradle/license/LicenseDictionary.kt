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

import com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef


class LicenseDictionary {

    private val byId: MutableMap<String, LicenseDefinition> = HashMap()
    private val byUrl: MutableMap<String, LicenseDefinition> = HashMap()
    private val byName: MutableMap<String, LicenseDefinition> = HashMap()

    fun addConfig(data: String) {
        val yaml = YAMLFactory()
        val mapper = let {
            val mapper = ObjectMapper(yaml)
            mapper.registerModule(KotlinModule())
            mapper.configure(ALLOW_UNQUOTED_FIELD_NAMES, true)
            mapper
        }
        val parser = yaml.createParser(data)
        val definitions = mapper.readValues(parser, jacksonTypeRef<LicenseDefinition>()).readAll()
        definitions.forEach { addDefinition(it) }
    }

    private fun addDefinition(input: LicenseDefinition) {
        val candidate = byId.getOrPut(input.id.toLowerCase()) { input }
        candidate.names.add(input.name)
        candidate.names.addAll(input.names)
        candidate.urls.add(input.url)
        candidate.urls.addAll(input.urls)

        byName[candidate.id.toLowerCase()] = candidate
        byName[candidate.name.toLowerCase()] = candidate
        candidate.names.forEach { byName[it.toLowerCase()] = candidate }

        byUrl[candidate.url.toLowerCase()] = candidate
        candidate.urls.forEach { byUrl[it.toLowerCase()] = candidate }
    }

    fun lookup(representation: String): LicenseDefinition? {
        val key = representation.toLowerCase()
        return byUrl[key] ?: byName[key]
    }

    fun knownLicenses(): MutableCollection<LicenseDefinition> {
        return byId.values
    }
}
