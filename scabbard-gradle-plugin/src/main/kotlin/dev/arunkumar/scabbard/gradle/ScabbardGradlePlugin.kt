package dev.arunkumar.scabbard.gradle

import dev.arunkumar.scabbard.gradle.compilerproperties.CompilerProperty
import dev.arunkumar.scabbard.gradle.compilerproperties.applyCompilerProperty
import dev.arunkumar.scabbard.gradle.processor.manageScabbardProcessor
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

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


  private fun Project.onScabbardStatusChanged() {
    setupProjects {
      manageScabbardProcessor(enabled = scabbardPluginExtension.enabled)
    }
  }

  private fun onCompilerPropertySet(compilerProperty: CompilerProperty<*>) {
    scabbardPluginExtension.ifEnabled {
      project.setupProjects {
        applyCompilerProperty(compilerProperty)
      }
    }
  }

  private fun Project.setupProjects(block: Project.() -> Unit) {
    if (this == rootProject) {
      subprojects(block)
    } else {
      block(this)
    }
  }

  companion object {
    internal const val SCABBARD = "scabbard"
    internal const val SCABBARD_PLUGIN_ID = "scabbard.gradle"
    internal const val KOTLIN_PLUGIN_ID = "kotlin"
    internal const val KAPT_PLUGIN_ID = "kotlin-kapt"
    internal const val JAVA_LIBRARY_PLUGIN_ID = "java-library"
    internal const val ANDROID_APP_PLUGIN_ID = "com.android.application"
    internal const val ANDROID_LIBRARY_PLUGIN_ID = "com.android.library"
  }
}