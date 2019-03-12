package com.namics.oss.gradle.license

public data class Dependency(
        var id: String,
        var licenses: List<License>) {

    override fun toString(): String {
        return "$id : \n" +
                licenses.joinToString(separator = "\t- ", postfix = "\n", prefix = "\t- ")
    }
}
