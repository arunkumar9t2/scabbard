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

class ScabbardPropertiesDelegate(
  private val project: Project,
  private val scabbardExtension: ScabbardExtension
) {

  private val singleGraph = "$SCABBARD.singleGraph"
  private val failOnError = "$SCABBARD.failOnError"

  fun delegate() {
    scabbardExtension.ifEnabled {
      project.afterEvaluate {
        when {
          isKotlinProject -> {
            val kaptExtension = extensions.findByType<KaptExtension>()
            if (kaptExtension != null) {
              kaptExtension.apply {
                arguments {
                  arg(singleGraph, scabbardExtension.singleGraph)
                  arg(failOnError, scabbardExtension.failOnError)
                }
              }
            } else {
              project.logger.error("Detected a Kotlin project but could not find KaptExtension")
            }
          }
          hasJavaAnnotationProcessorConfig -> {
            tasks.withType<JavaCompile>().configureEach {
              options.compilerArgs.apply {
                add("-A$singleGraph=${scabbardExtension.singleGraph}")
                add("-A$failOnError=${scabbardExtension.failOnError}")
              }
            }
          }
        }
      }
    }
  }
}