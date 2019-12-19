package dev.arunkumar.scabbard.gradle.processor

import dev.arunkumar.scabbard.gradle.ScabbardExtension
import dev.arunkumar.scabbard.gradle.projectmeta.ANNOTATION_PROCESSOR
import dev.arunkumar.scabbard.gradle.projectmeta.hasJavaAnnotationProcessorConfig
import dev.arunkumar.scabbard.gradle.projectmeta.isKotlinProject
import org.gradle.api.Project

//TODO(arun) Can this be provided via resources?
private const val SCABBARD_PROCESSOR = "dev.arunkumar:scabbard-processor:0.1.0"
private const val KAPT = "kapt"

class ScabbardProcessorManager(
  private val project: Project,
  private val scabbardExtension: ScabbardExtension
) {
  fun manage() {
    @Suppress("ControlFlowWithEmptyBody")
    if (scabbardExtension.enabled) {
      when {
        project.isKotlinProject -> project.dependencies.add(KAPT, SCABBARD_PROCESSOR)
        project.hasJavaAnnotationProcessorConfig -> project.dependencies.add(
          ANNOTATION_PROCESSOR,
          SCABBARD_PROCESSOR
        )
        else -> project.logger.error("Neither $ANNOTATION_PROCESSOR or $KAPT was found in project")
      }
    } else {
      //TODO(arun) Manually remove the dependency? It is possible for the dep to be added without plugin
    }
  }
}