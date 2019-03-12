package com.namics.oss.gradle.license

import com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
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
            mapper.registerModule(KotlinModule())
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
