package dev.arunkumar.scabbard.gradle.propertiesdelegate

import dev.arunkumar.scabbard.gradle.OutputFormat
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.JAVA_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.KAPT_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.SCABBARD
import dev.arunkumar.scabbard.gradle.ScabbardPluginExtension
import dev.arunkumar.scabbard.gradle.propertiesdelegate.CompilerProperty.Companion.FAIL_ON_ERROR_PROPERTY
import dev.arunkumar.scabbard.gradle.propertiesdelegate.CompilerProperty.Companion.FULL_GRAPH_VALIDATION_PROPERTY
import dev.arunkumar.scabbard.gradle.propertiesdelegate.CompilerProperty.Companion.OUTPUT_FORMAT_PROPERTY
import dev.arunkumar.scabbard.gradle.propertiesdelegate.CompilerProperty.Companion.QUALIFIED_NAMES_PROPERTY
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

data class CompilerProperty<T>(val name: String, val value: T) {
  companion object {
    internal const val FAIL_ON_ERROR_PROPERTY = "$SCABBARD.failOnError"
    internal const val QUALIFIED_NAMES_PROPERTY = "$SCABBARD.qualifiedNames"
    internal const val OUTPUT_FORMAT_PROPERTY = "$SCABBARD.outputFormat"
    internal const val FULL_GRAPH_VALIDATION_PROPERTY = "dagger.fullBindingGraphValidation"
  }
}

internal val FAIL_ON_ERROR = CompilerProperty(FAIL_ON_ERROR_PROPERTY, false)
internal val QUALIFIED_NAMES = CompilerProperty(QUALIFIED_NAMES_PROPERTY, false)
internal val OUTPUT_FORMAT = CompilerProperty(OUTPUT_FORMAT_PROPERTY, OutputFormat.PNG)
internal val FULL_GRAPH_VALIDATION = CompilerProperty(
  FULL_GRAPH_VALIDATION_PROPERTY,
  false
)
internal val FULL_GRAPH_VALIDATION_MAPPER: (Boolean) -> String? = { enabled ->
  if (enabled) {
    "WARNING"
  } else {
    null
  }
}

/**
 * A pass-through mapper function that forwards value as received without any modification
 */
internal fun <T> instanceMapper(): (T) -> T = { it }

internal fun <T> ScabbardPluginExtension.compilerProperty(
  compilerProperty: CompilerProperty<T>
) = mapCompilerProperty(compilerProperty, instanceMapper())

internal fun <T, R> ScabbardPluginExtension.mapCompilerProperty(
  compilerProperty: CompilerProperty<T>,
  valueMapper: (T) -> R
) = object : ObservableProperty<T>(compilerProperty.value) {
  override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
    if (oldValue != newValue) {
      val mappedValue = valueMapper(newValue)
      onCompilerPropertyChanged.execute(CompilerProperty(compilerProperty.name, mappedValue))
    }
  }
}

internal fun Project.applyCompilerProperty(compilerProperty: CompilerProperty<*>) {
  pluginManager.withPlugin(KAPT_PLUGIN_ID) {
    // For Kotlin projects, forward compiler arguments to Kapt
    logger.quiet("K: ${compilerProperty.name} is set to `${compilerProperty.value}` for ${project.name}")
    configure<KaptExtension> {
      arguments {
        compilerProperty.value?.let { value -> arg(compilerProperty.name, value) }
      }
    }
  }
  pluginManager.withPlugin(JAVA_PLUGIN_ID) {
    // For Java projects, forward to Java Compile tasks directly.
    logger.quiet("J: ${compilerProperty.name} is set to `${compilerProperty.value}` for ${project.name}")
    tasks.withType<JavaCompile>().configureEach {
      options.compilerArgs.apply {
        compilerProperty.value?.let { value -> add("-A${compilerProperty.name}=$value") }
      }
    }
  }
}