package dev.arunkumar.scabbard.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class ScabbardGradlePlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.extensions.create(
      SCABBARD,
      ScabbardPluginExtension::class.java,
      project
    )
  }

  companion object {
    const val SCABBARD = "scabbard"
    const val PLUGIN_ID = "scabbard-gradle-plugin"
  }
}