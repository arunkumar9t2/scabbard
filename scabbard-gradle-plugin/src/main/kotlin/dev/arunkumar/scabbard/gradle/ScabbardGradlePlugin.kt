package dev.arunkumar.scabbard.gradle

import dev.arunkumar.scabbard.gradle.compilerproperties.CompilerProperty
import dev.arunkumar.scabbard.gradle.compilerproperties.applyCompilerProperty
import dev.arunkumar.scabbard.gradle.processor.manageScabbardProcessor
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * A gradle plugin that configures the project for Dagger 2 graph visualization generation during build.
 *
 * Usage: The plugin can be configured by adding the following extension block.
 * ```
 * scabbard {
 *    enabled = true
 * }
 * ```
 *
 * How it works:
 * - It works by adding Scabbard's annotation processor to your project's dependencies in either
 * `annotationProcessor` or `kapt` configuration based on the project type.
 * - Enables configuring the annotation processor behavior by delegating user defined values received
 * via [ScabbardPluginExtension] to [JavaCompile] or [KotlinCompile] tasks
 * - At the moment, the plugin does not add any new tasks to the project.
 *
 * Most of the work happens lazily by confirming to task configuration avoidance.
 *
 * Results:
 * After a successful build, the generated dagger visualization images should be present in
 * - Java : `build/classes/java/$sourceSet/scabbard`
 * - Kotlin: `build/tmp/kapt3/classes/$sourceSet/scabbard`
 */
class ScabbardGradlePlugin : Plugin<Project> {

  private lateinit var scabbardPluginExtension: ScabbardPluginExtension

  override fun apply(project: Project) {
    scabbardPluginExtension = project.extensions.create(
      SCABBARD,
      ScabbardPluginExtension::class.java,
      project,
      Action<Boolean> { project.onScabbardStatusChanged() },
      Action<CompilerProperty<*>> { onCompilerPropertySet(this) }
    )
  }

  /**
   * Callback to configure Scabbard's annotation processor dependency when gradle configures
   * [ScabbardPluginExtension]
   */
  private fun Project.onScabbardStatusChanged() {
    setupProjects {
      manageScabbardProcessor(enabled = scabbardPluginExtension.enabled)
    }
  }

  /**
   * Applies the given [compilerProperty] to all projects defined by [setupProjects] based on
   * whether the plugin is enabled or not.
   */
  private fun onCompilerPropertySet(compilerProperty: CompilerProperty<*>) {
    scabbardPluginExtension.ifEnabled {
      project.setupProjects {
        applyCompilerProperty(compilerProperty)
      }
    }
  }

  /**
   * Simple extension to handle setting up different project modules based on where the plugin is applied.
   * If it is a root project, all the subprojects are automatically configured.
   */
  private fun Project.setupProjects(block: Project.() -> Unit) {
    block(this)
    if (this == rootProject) {
      subprojects(block)
    }
  }

  companion object {
    internal const val SCABBARD = "scabbard"
    internal const val SCABBARD_PLUGIN_ID = "scabbard.gradle"
    internal const val KOTLIN_PLUGIN_ID = "kotlin"
    internal const val KAPT_PLUGIN_ID = "kotlin-kapt"
    internal const val JAVA_PLUGIN_ID = "java"
    internal const val JAVA_LIBRARY_PLUGIN_ID = "java-library"
    internal const val ANDROID_APP_PLUGIN_ID = "com.android.application"
    internal const val ANDROID_LIBRARY_PLUGIN_ID = "com.android.library"
  }
}
