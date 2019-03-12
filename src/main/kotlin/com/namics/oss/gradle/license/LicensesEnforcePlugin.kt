package com.namics.oss.gradle.license

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

public class LicensesEnforcePlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        tasks.register("enforceLicenses", LicenseEnforceTask::class){

        }
    }
}
