package dev.arunkumar.scabbard.gradle

import dev.arunkumar.scabbard.gradle.processor.ScabbardProcessorManager
import dev.arunkumar.scabbard.gradle.propertiesdelegate.ScabbardPropertiesDelegate
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

class ScabbardGradlePlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.extensions.create(
      SCABBARD,
      ScabbardPluginExtension::class.java,
      project,
      project.objects,
      Action(::onExtensionConfigured)
    )
  }

  /**
   * Lifecycle method that gets called when Gradle configures [ScabbardPluginExtension] and user
   * defined properties are attached to the extension.
   *
   * This method is still called during configuration phase and it is safe to read customized properties
   * from extension.
   *
   * The same behavior could be acheived by using [Project.afterEvaluate] but that has some issues
   * when working with `kapt` compiler arguments.
   */
  private fun onExtensionConfigured(extension: ScabbardPluginExtension) {
    ScabbardProcessorManager(extension).manage()
    ScabbardPropertiesDelegate(extension).delegate()
  }

  companion object {
    internal const val SCABBARD = "scabbard"
    internal const val SCABBARD_PLUGIN_ID = "scabbard.gradle"
    internal const val KOTLIN_PLUGIN_ID = "kotlin"
    internal const val KAPT_PLUGIN_ID = "kotlin-kapt"
    internal const val JAVA_PLUGIN_ID = "java"
  }
}