package dev.arunkumar.scabbard.gradle.processor

import dev.arunkumar.scabbard.gradle.DefaultScabbardSpec
import dev.arunkumar.scabbard.gradle.projectmeta.ANNOTATION_PROCESSOR
import dev.arunkumar.scabbard.gradle.projectmeta.hasJavaAnnotationProcessorConfig
import dev.arunkumar.scabbard.gradle.projectmeta.isKotlinProject

//TODO(arun) Can this be provided via resources?
const val SCABBARD_PROCESSOR = "dev.arunkumar:scabbard-processor:0.1.0"
const val KAPT = "kapt"

class ScabbardProcessorManager(private val scabbardSpec: DefaultScabbardSpec) {

  private val project get() = scabbardSpec.project

  fun manage() {
    @Suppress("ControlFlowWithEmptyBody")
    if (scabbardSpec.isScabbardEnabled) {
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