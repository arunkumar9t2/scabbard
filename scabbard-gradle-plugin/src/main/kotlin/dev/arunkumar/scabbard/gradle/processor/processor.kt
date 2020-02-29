package dev.arunkumar.scabbard.gradle.processor

import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.ANDROID_APP_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.ANDROID_LIBRARY_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.JAVA_LIBRARY_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.KAPT_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.SCABBARD_PLUGIN_ID
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency

internal const val KAPT_CONFIG = "kapt"
internal const val ANNOTATION_PROCESSOR_CONFIG = "annotationProcessor"
internal const val SCABBARD_GROUP = "dev.arunkumar"
internal const val SCABBARD_NAME = "scabbard-processor"
internal const val SCABBARD_PROCESSOR_FORMAT = "$SCABBARD_GROUP:$SCABBARD_NAME:%s"

private fun Dependency.isScabbardDependency() =
  group == SCABBARD_GROUP && name == SCABBARD_NAME

private fun Configuration.hasScabbard() = dependencies.any(Dependency::isScabbardDependency)

private fun configName(isKapt: Boolean) = when {
  isKapt -> KAPT_CONFIG
  else -> ANNOTATION_PROCESSOR_CONFIG
}

private fun Project.removeScabbard(isKapt: Boolean = false) {
  val config = configName(isKapt)
  configurations.findByName(config)
    ?.dependencies
    ?.iterator()
    ?.let { iterator ->
      for (dependency in iterator) {
        if (dependency.isScabbardDependency()) {
          iterator.remove()
          logger.info("Removed Scabbard from $name")
        }
      }
    }
}

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

internal fun Project.manageScabbardProcessor(enabled: Boolean) {
  val scabbardVersion = VersionCalculator(project).calculate()
  val scabbardDependency = SCABBARD_PROCESSOR_FORMAT.format(scabbardVersion)
  if (enabled) {
    fun Project.addScabbard(isKapt: Boolean = false) {
      val annotationConfig = configName(isKapt)
      if (configurations.findByName(annotationConfig) != null) {
        if (!configurations.getByName(annotationConfig).hasScabbard()) {
          logger.info("Adding scabbard to $annotationConfig config")
          dependencies.add(annotationConfig, scabbardDependency)
        }
      } else {
        logger.info("Skipped adding Scabbard due to lack of $annotationConfig config")
      }
    }
    pluginManager.run {
      withPlugin(ANDROID_APP_PLUGIN_ID) { addScabbard() }
      withPlugin(ANDROID_LIBRARY_PLUGIN_ID) { addScabbard() }
      withPlugin(JAVA_LIBRARY_PLUGIN_ID) { addScabbard() }
      withPlugin(KAPT_PLUGIN_ID) {
        removeScabbard() // Remove from annotationProcessor and add to kapt alone.
        addScabbard(isKapt = true)
      }
    }
  } else {
    // Even though by default we don't add the annotation processor dependency, it is possible the
    // dep was added by other means. Hence manually remove the dependency if it is found.
    removeScabbard()
    removeScabbard(isKapt = true)
  }
}