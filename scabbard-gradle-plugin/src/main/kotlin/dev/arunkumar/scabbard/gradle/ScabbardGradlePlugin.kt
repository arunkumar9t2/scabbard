package dev.arunkumar.scabbard.gradle

import dev.arunkumar.scabbard.gradle.processor.ScabbardProcessorManager
import dev.arunkumar.scabbard.gradle.propertiesdelegate.ScabbardPropertiesDelegate
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

@Suppress("unused")
class ScabbardGradlePlugin : Plugin<Project> {

  companion object {
    const val SCABBARD = "scabbard"
    const val PLUGIN_ID = "scabbard-gradle-plugin"
  }

  override fun apply(project: Project) {
    val scabbardExtension = project.extensions.create<ScabbardExtension>(SCABBARD)
    project.afterEvaluate {
      ScabbardProcessorManager(project, scabbardExtension).manage()
      ScabbardPropertiesDelegate(project, scabbardExtension).delegate()
    }
  }
}