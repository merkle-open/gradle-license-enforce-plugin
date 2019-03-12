package com.namics.oss.gradle.license

public data class License(var name: String,
                          var url: String) {
    override fun toString(): String {
        return "$name ($url)"
    }
}
