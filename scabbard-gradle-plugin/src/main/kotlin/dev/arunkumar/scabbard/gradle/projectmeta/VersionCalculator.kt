package dev.arunkumar.scabbard.gradle.projectmeta

import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer

class VersionCalculator(private val project: Project) {

  companion object {
    private const val CLASSPATH_CONFIG = "classpath"
  }

  private fun versionFromConfigurations(configurations: ConfigurationContainer): String? {
    return try {
      configurations.getByName(CLASSPATH_CONFIG)
        .dependencies.first { it.name == ScabbardGradlePlugin.PLUGIN_ID }
        .version.takeIf { it?.trim()?.isNotEmpty() == true }
    } catch (e: Exception) {
      null
    }
  }

  fun calculate(): String {
    val rootProjectConfigs = project.rootProject.buildscript.configurations
    val currentProjectConfigs = project.buildscript.configurations
    val versionFromRootProject = versionFromConfigurations(rootProjectConfigs)
    //.also { project.logger.quiet("versionFromRootProject:$it") }
    val versionFromCurrentProject = versionFromConfigurations(currentProjectConfigs)
    //.also { project.logger.quiet("versionFromCurrentProject:$it") }
    return versionFromCurrentProject ?: versionFromRootProject ?: "0.0.1" // <- worst case
  }
}