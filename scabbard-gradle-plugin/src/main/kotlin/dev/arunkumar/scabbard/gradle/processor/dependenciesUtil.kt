package dev.arunkumar.scabbard.gradle.processor

import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.SCABBARD_PLUGIN_ID
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency

internal const val SCABBARD_GROUP = "dev.arunkumar"
internal const val SCABBARD_NAME = "scabbard-processor"
internal const val SCABBARD_PROCESSOR_FORMAT = "$SCABBARD_GROUP:$SCABBARD_NAME:%s"

internal fun Dependency.isScabbardDependency() =
  group == SCABBARD_GROUP && name == SCABBARD_NAME

internal fun Dependency.isDaggerCompiler() =
  group == "com.google.dagger" && name == "dagger-compiler"

internal class VersionCalculator(private val project: Project) {

  companion object {
    private const val CLASSPATH_CONFIG = "classpath"
  }

  private fun versionFromConfigurations(configurations: ConfigurationContainer): String? {
    return try {
      configurations.getByName(CLASSPATH_CONFIG)
        .dependencies.first { it.name == SCABBARD_PLUGIN_ID }
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
    return versionFromCurrentProject ?: versionFromRootProject ?: "0.1.0" // <- worst case
  }
}