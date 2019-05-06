package com.namics.oss.gradle.license

public data class Dependency(
        val id: String,
        val licenses: List<License>,
        val group: String = id.split(":")[0],
        val module: String? = id.split(":").getOrNull(1),
        val version: String? = id.split(":").getOrNull(2)) {

    override fun toString(): String {
        return "$id : \n" +
                licenses.joinToString(separator = "\t- ", postfix = "\n", prefix = "\t- ")
    }

    fun matches(dependency: Dependency): Boolean {
        return dependency.id == id
                || (dependency.group == group
                && dependency.module == module
                && (dependency.module == null || dependency.module == module)
                )
    }
}
