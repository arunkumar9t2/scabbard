package dev.arunkumar.scabbard.gradle.processor

import dev.arunkumar.scabbard.gradle.DefaultScabbardSpec
import dev.arunkumar.scabbard.gradle.projectmeta.ANNOTATION_PROCESSOR
import dev.arunkumar.scabbard.gradle.projectmeta.VersionCalculator
import dev.arunkumar.scabbard.gradle.projectmeta.hasJavaAnnotationProcessorConfig
import dev.arunkumar.scabbard.gradle.projectmeta.isKotlinProject

//TODO(arun) Can this be provided via resources?
internal const val SCABBARD_PROCESSOR_FORMAT = "dev.arunkumar:scabbard-processor:%s"
internal const val KAPT = "kapt"

/**
 * Responsible for applying Scabbard processor dependency based of project metadata. For example,
 * `kapt` for Kotlin projects and `annotationProcessor` for pure Java projects.
 */
internal class ScabbardProcessorManager(private val scabbardSpec: DefaultScabbardSpec) {

  private val project get() = scabbardSpec.project

  fun manage() {
    @Suppress("ControlFlowWithEmptyBody")
    if (scabbardSpec.isScabbardEnabled) {
      val scabbardVersion = VersionCalculator(project).calculate()
      val scabbardDependency = SCABBARD_PROCESSOR_FORMAT.format(scabbardVersion)
      project.logger.info("Applying scabbard dependency: $scabbardDependency")
      when {
        project.isKotlinProject -> project.dependencies.add(KAPT, scabbardDependency)
        project.hasJavaAnnotationProcessorConfig -> project.dependencies.add(
          ANNOTATION_PROCESSOR,
          scabbardDependency
        )
        else -> project.logger.error("Neither $ANNOTATION_PROCESSOR or $KAPT was found in project")
      }
    } else {
      //TODO(arun) Manually remove the dependency? It is possible for the dep to be added without plugin
    }
  }
}