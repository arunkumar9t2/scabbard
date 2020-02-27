package dev.arunkumar.scabbard.gradle

import dev.arunkumar.scabbard.gradle.propertiesdelegate.CompilerProperty
import dev.arunkumar.scabbard.gradle.propertiesdelegate.applyCompilerProperty
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

class ScabbardGradlePlugin : Plugin<Project> {

  lateinit var scabbardPluginExtension: ScabbardPluginExtension

  override fun apply(project: Project) {
    scabbardPluginExtension = project.extensions.create(
      SCABBARD,
      ScabbardPluginExtension::class.java,
      project,
      Action<CompilerProperty<*>> { onCompilerPropertySet(project, this) }
    )
  }

  private fun onCompilerPropertySet(
    project: Project,
    compilerProperty: CompilerProperty<*>
  ) {
    scabbardPluginExtension.ifEnabled {
      if (project != project.rootProject) {
        project.applyCompilerProperty(compilerProperty)
      } else {
        project.subprojects { applyCompilerProperty(compilerProperty) }
      }
    }
  }

  companion object {
    internal const val SCABBARD = "scabbard"
    internal const val SCABBARD_PLUGIN_ID = "scabbard.gradle"
    internal const val KOTLIN_PLUGIN_ID = "kotlin"
    internal const val KAPT_PLUGIN_ID = "kotlin-kapt"
    internal const val JAVA_PLUGIN_ID = "java-library"
  }
}