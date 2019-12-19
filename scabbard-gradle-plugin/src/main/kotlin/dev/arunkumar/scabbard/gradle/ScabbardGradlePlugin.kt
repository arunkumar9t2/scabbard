package dev.arunkumar.scabbard.gradle

import dev.arunkumar.scabbard.gradle.processor.ScabbardProcessorManager
import dev.arunkumar.scabbard.gradle.propertiesdelegate.ScabbardPropertiesDelegate
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

const val SCABBARD = "scabbard"

@Suppress("unused")
class ScabbardGradlePlugin : Plugin<Project> {

  override fun apply(project: Project) {
    val scabbardExtension = project.extensions.create<ScabbardExtension>(SCABBARD)
    ScabbardProcessorManager(project, scabbardExtension).manage()
    ScabbardPropertiesDelegate(project, scabbardExtension).delegate()
  }
}