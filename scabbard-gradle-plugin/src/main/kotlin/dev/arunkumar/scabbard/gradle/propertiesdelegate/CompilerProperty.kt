package dev.arunkumar.scabbard.gradle.propertiesdelegate

import dev.arunkumar.scabbard.gradle.OutputFormat
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.JAVA_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.KAPT_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.SCABBARD
import dev.arunkumar.scabbard.gradle.ScabbardPluginExtension
import dev.arunkumar.scabbard.gradle.propertiesdelegate.CompilerProperty.Companion.FAIL_ON_ERROR_PROPERTY
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
    internal const val DAGGER_FULL_BINDING_GRAPH_VALIDATION =
      "dagger.fullBindingGraphValidation=WARNING"
  }
}

internal val FAIL_ON_ERROR = CompilerProperty(FAIL_ON_ERROR_PROPERTY, false)
internal val QUALIFIED_NAMES = CompilerProperty(QUALIFIED_NAMES_PROPERTY, false)
internal val OUTPUT_FORMAT = CompilerProperty(OUTPUT_FORMAT_PROPERTY, OutputFormat.PNG)

internal fun <T> ScabbardPluginExtension.compilerProperty(
  compilerProperty: CompilerProperty<T>
) = object : ObservableProperty<T>(compilerProperty.value) {
  override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
    if (oldValue != newValue) {
      onCompilerPropertyChanged.execute(compilerProperty.copy(value = newValue))
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