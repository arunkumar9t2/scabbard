package dev.arunkumar.scabbard.gradle.processor

import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.ANDROID_APP_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.ANDROID_LIBRARY_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.JAVA_LIBRARY_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.KAPT_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.plugin.VERSION
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.plugins.PluginManager

internal const val KAPT_CONFIG = "kapt"
internal const val ANNOTATION_PROCESSOR_CONFIG = "annotationProcessor"
internal const val SCABBARD_GROUP = "dev.arunkumar"
internal const val SCABBARD_NAME = "scabbard-processor"
internal const val SCABBARD_PROCESSOR_DEPENDENCY = "$SCABBARD_GROUP:$SCABBARD_NAME:$VERSION"

/**
 * @return if the [Dependency] is Scabbard's annotation processor dependency
 */
private fun Dependency.isScabbardDependency() =
  group == SCABBARD_GROUP && name == SCABBARD_NAME

/**
 * @return Boolean if the Configuration has Scabbard's annotation processor dependency
 */
private fun Configuration.hasScabbard() = dependencies.any(Dependency::isScabbardDependency)

private fun configName(isKapt: Boolean) = when {
  isKapt -> KAPT_CONFIG
  else -> ANNOTATION_PROCESSOR_CONFIG
}

/**
 * Removes the Scabbard annotation processor dependency from this project. [isKapt] controls which
 * config to remove from.
 *
 * @param isKapt when true the dependency is removed from `kapt`, if not it is removed from
 * `annotationProcessor`
 */
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

/**
 * Add scabbard processor dependency to the project in either `annotationProcessor` or `kapt`
 * configuration as determined by [isKapt]
 *
 * The dependency is added only if the configuration itself is already available, if not it is not
 * added.
 */
private fun Project.addScabbard(isKapt: Boolean = false) {
  val annotationConfig = configName(isKapt)
  if (configurations.findByName(annotationConfig) != null) {
    if (!configurations.getByName(annotationConfig).hasScabbard()) {
      logger.info("Adding scabbard to $annotationConfig config")
      dependencies.add(annotationConfig, SCABBARD_PROCESSOR_DEPENDENCY)
    }
  } else {
    logger.info("Skipped adding Scabbard due to lack of $annotationConfig config")
  }
}

/**
 * Responsible for adding Scabbard's annotation processor to the Project based on project properties
 * such as if it is as `java`, `kotlin`, `android` or a mixed project.
 *
 * The logic relies on callback's from Gradle's [PluginManager] API to determine the course of action
 * to avoid reliance on plugin application order to determine other applied plugins. Relying on plugin
 * application order can have issue when using `subprojects` or `allprojects`
 *
 * The dependency is added without forcing any resolution of other dependencies.
 *
 * @param enabled control whether the processor to be added to this project. If `false` any dependency
 * on Scabbard's annotation processor will be removed from this project. Even if it was not added by
 * this plugin.
 */
internal fun Project.manageScabbardProcessor(enabled: Boolean) {
  if (enabled) {
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