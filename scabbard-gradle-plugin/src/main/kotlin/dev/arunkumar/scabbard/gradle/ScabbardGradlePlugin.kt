package dev.arunkumar.scabbard.gradle

import dev.arunkumar.scabbard.gradle.processor.ScabbardProcessorManager
import dev.arunkumar.scabbard.gradle.propertiesdelegate.ScabbardPropertiesDelegate
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType

const val SCABBARD = "scabbard"
const val PLUGIN_ID = "scabbard-gradle-plugin"

@Suppress("unused")
class ScabbardGradlePlugin : Plugin<Project> {

  private fun Project.getOrCreateExtension(): ScabbardExtension {
    return extensions.findByType() ?: project.extensions.create(SCABBARD)
  }

  override fun apply(project: Project) {
    val scabbardExtension = project.getOrCreateExtension()
    ScabbardProcessorManager(project, scabbardExtension).manage()
    ScabbardPropertiesDelegate(project, scabbardExtension).delegate()
  }
}