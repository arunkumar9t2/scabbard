package dev.arunkumar.scabbard.gradle.propertiesdelegate

import dev.arunkumar.scabbard.gradle.OutputFormat
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.SCABBARD
import dev.arunkumar.scabbard.gradle.ScabbardPluginExtension
import dev.arunkumar.scabbard.gradle.projectmeta.hasJavaAnnotationProcessorConfig
import dev.arunkumar.scabbard.gradle.projectmeta.isKotlinProject
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

internal class ScabbardPropertiesDelegate(private val scabbardPluginExtension: ScabbardPluginExtension) {

  companion object {
    internal const val FAIL_ON_ERROR = "$SCABBARD.failOnError"
    internal const val QUALIFIED_NAMES = "$SCABBARD.qualifiedNames"
    internal const val DAGGER_FULL_BINDING_GRAPH_VALIDATION =
      "dagger.fullBindingGraphValidation=WARNING"
    internal const val OUTPUT_FORMAT = "$SCABBARD.outputFormat"
  }

  private val project get() = scabbardPluginExtension.project

  fun delegate() {
    if (scabbardPluginExtension.enabled) {
      project.run {
        when {
          isKotlinProject -> {
            val kaptExtension = extensions.findByType<KaptExtension>()
            kaptExtension?.apply {
              arguments {
                arg(FAIL_ON_ERROR, scabbardPluginExtension.failOnError)
                arg(QUALIFIED_NAMES, scabbardPluginExtension.qualifiedNames)
                arg(OUTPUT_FORMAT, OutputFormat.parse(scabbardPluginExtension.outputFormat))

                if (scabbardPluginExtension.fullBindingGraphValidation) {
                  arg(DAGGER_FULL_BINDING_GRAPH_VALIDATION)
                }
              }
            } ?: project.logger.error("Detected a Kotlin project but could not find KaptExtension")
          }
          hasJavaAnnotationProcessorConfig -> {
            tasks.withType<JavaCompile>().configureEach {
              options.compilerArgs.apply {
                add("-A$FAIL_ON_ERROR=${scabbardPluginExtension.failOnError}")
                add("-A$QUALIFIED_NAMES=${scabbardPluginExtension.qualifiedNames}")
                add("-A$OUTPUT_FORMAT=${OutputFormat.parse(scabbardPluginExtension.outputFormat)}")

                if (scabbardPluginExtension.fullBindingGraphValidation) {
                  add("-A$DAGGER_FULL_BINDING_GRAPH_VALIDATION")
                }
              }
            }
          }
          else -> {

          }
        }
      }
    }
  }
}