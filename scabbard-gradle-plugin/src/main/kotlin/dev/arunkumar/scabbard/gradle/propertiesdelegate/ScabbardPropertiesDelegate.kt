package dev.arunkumar.scabbard.gradle.propertiesdelegate

import dev.arunkumar.scabbard.gradle.SCABBARD
import dev.arunkumar.scabbard.gradle.ScabbardExtension
import dev.arunkumar.scabbard.gradle.projectmeta.hasJavaAnnotationProcessorConfig
import dev.arunkumar.scabbard.gradle.projectmeta.isKotlinProject
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

internal const val SINGLE_GRAPH = "$SCABBARD.singleGraph"
internal const val FAIL_ON_ERROR = "$SCABBARD.failOnError"

class ScabbardPropertiesDelegate(
  private val project: Project,
  private val scabbardExtension: ScabbardExtension
) {
  fun delegate() {
    scabbardExtension.ifEnabled {
      project.run {
        when {
          isKotlinProject -> {
            val kaptExtension = extensions.findByType<KaptExtension>()
            if (kaptExtension != null) {
              kaptExtension.apply {
                arguments {
                  arg(SINGLE_GRAPH, scabbardExtension.singleGraph)
                  arg(FAIL_ON_ERROR, scabbardExtension.failOnError)
                }
              }
            } else {
              project.logger.error("Detected a Kotlin project but could not find KaptExtension")
            }
          }
          hasJavaAnnotationProcessorConfig -> {
            tasks.withType<JavaCompile>().configureEach {
              options.compilerArgs.apply {
                add("-A$SINGLE_GRAPH=${scabbardExtension.singleGraph}")
                add("-A$FAIL_ON_ERROR=${scabbardExtension.failOnError}")
              }
            }
          }
        }
      }
    }
  }
}