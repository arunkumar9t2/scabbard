/*
 * Copyright 2022 Arunkumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.arunkumar.scabbard.gradle.options

import dev.arunkumar.scabbard.gradle.ScabbardPluginExtension
import dev.arunkumar.scabbard.gradle.options.JvmCompilerProperty.Companion.FAIL_ON_ERROR_PROPERTY
import dev.arunkumar.scabbard.gradle.options.JvmCompilerProperty.Companion.OUTPUT_FORMAT_PROPERTY
import dev.arunkumar.scabbard.gradle.options.JvmCompilerProperty.Companion.QUALIFIED_NAMES_PROPERTY
import dev.arunkumar.scabbard.gradle.output.OutputFormat
import dev.arunkumar.scabbard.gradle.processor.ANNOTATION_PROCESSOR_CONFIG
import dev.arunkumar.scabbard.gradle.processor.isDaggerCompiler
import dev.arunkumar.scabbard.gradle.util.JAVA_LIBRARY_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.util.KAPT_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.util.SCABBARD
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import kotlin.properties.Delegates.observable
import kotlin.properties.ReadWriteProperty

/**
 * Type safe structure to define Java/Kotlin property
 *
 * @param name the name of the property
 * @param value the value for this property. The type is usually
 *     converted to string using [toString] hence care
 *     must for taken when custom object is used.
 */
data class JvmCompilerProperty<T>(
  val name: String,
  val value: T
) {
  companion object {
    // TODO(arun) Share constants via common module
    internal const val FAIL_ON_ERROR_PROPERTY = "$SCABBARD.failOnError"
    internal const val QUALIFIED_NAMES_PROPERTY = "$SCABBARD.qualifiedNames"
    internal const val OUTPUT_FORMAT_PROPERTY = "$SCABBARD.outputFormat"
    internal const val FULL_GRAPH_VALIDATION_PROPERTY = "dagger.fullBindingGraphValidation"
  }
}

internal val FAIL_ON_ERROR = JvmCompilerProperty(FAIL_ON_ERROR_PROPERTY, false)
internal val QUALIFIED_NAMES = JvmCompilerProperty(QUALIFIED_NAMES_PROPERTY, false)
internal val OUTPUT_FORMAT = JvmCompilerProperty(OUTPUT_FORMAT_PROPERTY, OutputFormat.PNG)

/**
 * A mapper function that forwards value as received without any
 * modification
 */
@Suppress("FunctionName")
internal fun <T> PassThroughMapper(): (T) -> T = { it }

internal fun <T> ScabbardPluginExtension.jvmCompilerProperty(
  jvmCompilerProperty: JvmCompilerProperty<T>
) = mapCompilerProperty(jvmCompilerProperty, PassThroughMapper())

/**
 * A property delegate that calls
 * [ScabbardPluginExtension.onJvmCompilerPropertyChanged] whenever the
 * backing value changes and broadcasts it as `CompilerProperty<T>`
 * where `T` is the type of the property. Optionally it is possible
 * to convert the property type from one to another by using
 * [valueMapper]. For example, see `[DAGGER_FULL_GRAPH_VALIDATION] and
 * [DAGGER_FULL_GRAPH_VALIDATION_MAPPER].
 *
 * Note: The change is broadcast only if the value actually changes.
 *
 * @param jvmCompilerProperty initial defaults of the compiler property
 * @param valueMapper a value mapper that will be executed before
 *     broadcasting change to
 *     [ScabbardPluginExtension.onCompilerPropertyChanged]
 */
internal fun <T, R> ScabbardPluginExtension.mapCompilerProperty(
  jvmCompilerProperty: JvmCompilerProperty<T>,
  valueMapper: (T) -> R
): ReadWriteProperty<Any?, T> = observable(jvmCompilerProperty.value) { _, oldValue, newValue ->
  if (oldValue != newValue) {
    val mappedValue = valueMapper(newValue)
    onJvmCompilerPropertyChanged.execute(JvmCompilerProperty(jvmCompilerProperty.name, mappedValue))
  }
}

/**
 * Applies the given [jvmCompilerProperty] to this project.
 *
 * For `Java` projects, the properties are directly set to
 * [JavaCompile]'s compiler arguments. For `Kotlin` projects, the
 * properties are set in [KaptExtension]'s arguments.
 */
internal fun Project.applyCompilerProperty(jvmCompilerProperty: JvmCompilerProperty<*>) {
  pluginManager.withPlugin(KAPT_PLUGIN_ID) {
    // For Kotlin projects, forward compiler arguments to Kapt
    configure<KaptExtension> {
      arguments {
        jvmCompilerProperty.value?.let { value -> arg(jvmCompilerProperty.name, value) }
      }
    }
  }
  pluginManager.withPlugin(JAVA_LIBRARY_PLUGIN_ID) {
    // For Java projects, forward to Java Compile tasks directly.
    configurations.findByName(ANNOTATION_PROCESSOR_CONFIG)?.withDependencies {
      if (any(Dependency::isDaggerCompiler)) {
        tasks.withType<JavaCompile>().configureEach {
          options.compilerArgs.apply {
            jvmCompilerProperty.value?.let { value ->
              add("-A${jvmCompilerProperty.name}=$value")
            }
          }
        }
      }
    }
  }
}
