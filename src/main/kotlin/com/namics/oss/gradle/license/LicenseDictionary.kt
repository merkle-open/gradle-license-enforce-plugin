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
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef


public class LicenseDictionary {

    private val byId: MutableMap<String, LicenseDefinition> = HashMap()
    private val byUrl: MutableMap<String, LicenseDefinition> = HashMap()
    private val byName: MutableMap<String, LicenseDefinition> = HashMap()

    fun addConfig(data: String) {
        val yaml = YAMLFactory()
        val mapper = let {
            val mapper = ObjectMapper(yaml)
            mapper.registerModule(
                KotlinModule.Builder()
                    .withReflectionCacheSize(512)
                    .configure(KotlinFeature.NullToEmptyCollection, false)
                    .configure(KotlinFeature.NullToEmptyMap, false)
                    .configure(KotlinFeature.NullIsSameAsDefault, false)
                    .configure(KotlinFeature.SingletonSupport, false)
                    .configure(KotlinFeature.StrictNullChecks, false)
                    .build()
            )
            mapper.configure(ALLOW_UNQUOTED_FIELD_NAMES, true)
            mapper
        }
        val parser = yaml.createParser(data)
        val definitions = mapper.readValues<LicenseDefinition>(parser, jacksonTypeRef<LicenseDefinition>()).readAll()
        definitions.forEach { addDefintion(it) }
    }

    private fun addDefintion(input: LicenseDefinition) {
        val candidate = byId.getOrPut(input.id.toLowerCase()) { input }
        candidate.names.add(input.name)
        candidate.names.addAll(input.names)
        candidate.urls.add(input.url)
        candidate.urls.addAll(input.urls)

        byName.put(candidate.id.toLowerCase(), candidate)
        byName.put(candidate.name.toLowerCase(), candidate)
        candidate.names.forEach { byName.put(it.toLowerCase(), candidate) }

        byUrl.put(candidate.url.toLowerCase(), candidate)
        candidate.urls.forEach { byUrl.put(it.toLowerCase(), candidate) }
    }

    fun lookup(representation: String): LicenseDefinition? {
        val key = representation.toLowerCase()
        return byUrl.get(key) ?: byName.get(key)
    }

    fun knownLicenses(): MutableCollection<LicenseDefinition> {
        return byId.values
    }
}
