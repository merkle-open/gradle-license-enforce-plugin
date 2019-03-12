package com.namics.oss.gradle.license

public data class LicenseDefinition(val id: String,
                                    val category: String,
                                    val name: String,
                                    val url: String,
                                    val names : MutableList<String> = mutableListOf(),
                                    val urls : MutableList<String> = mutableListOf()) {
}
