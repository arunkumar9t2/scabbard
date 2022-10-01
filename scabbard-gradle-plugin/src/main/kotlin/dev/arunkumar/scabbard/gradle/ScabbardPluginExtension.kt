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

package dev.arunkumar.scabbard.gradle

import dev.arunkumar.scabbard.gradle.options.*
import dev.arunkumar.scabbard.gradle.options.FAIL_ON_ERROR
import dev.arunkumar.scabbard.gradle.options.OUTPUT_FORMAT
import dev.arunkumar.scabbard.gradle.options.QUALIFIED_NAMES
import dev.arunkumar.scabbard.gradle.options.jvmCompilerProperty
import dev.arunkumar.scabbard.gradle.options.mapCompilerProperty
import dev.arunkumar.scabbard.gradle.output.OutputFormat
import dev.arunkumar.scabbard.gradle.util.scabbardEnabledProperty
import org.gradle.api.Action
import org.gradle.api.Project

/**
 * Scabbard plugin extension that is used to receive user preferences
 * and configure the plugin accordingly.
 *
 * This class is only instantiated by Gradle and dependencies are
 * resolved automatically by injection.
 *
 * @see [ScabbardGradlePlugin] for information.
 * @param project The project instance where the plugin is applied
 * @param onScabbardEnabledStatusChanged The action to execute when
 *     [ScabbardPluginExtension.enabled] is configured
 * @param onJvmCompilerPropertyChanged The action to execute when any of
 *     the compiler property is configured.
 */
open class ScabbardPluginExtension(
  val project: Project,
  internal val onScabbardEnabledStatusChanged: Action<Boolean>,
  internal val onJvmCompilerPropertyChanged: Action<JvmCompilerProperty<*>>
) {

  /**
   * Control whether scabbard is enabled or not. Default value is
   * `false`.
   */
  open var enabled by scabbardEnabledProperty()

  /**
   * By default, scabbard does not fail the build when any error occurs
   * in scabbard's processor. Setting this property to `true` will
   * change that behaviour to fail on any error for debugging purposes.
   */
  open var failOnError by jvmCompilerProperty(FAIL_ON_ERROR)

  /**
   * Flag to control if fully qualified names should be used everywhere
   * in the graph. Default value is `false`
   */
  open var qualifiedNames by jvmCompilerProperty(QUALIFIED_NAMES)

  /**
   * Configures Dagger processor to do full graph validation which
   * processes each `@Module`, `@Component` and `@Subcomponent`. This
   * enables visualization of missing bindings and generates graphs for
   * `@Module` too.
   *
   * @see [https://dagger.dev/compiler-options.html]
   */
  open var fullBindingGraphValidation by daggerFullGraphValidationProperty()

  /**
   * The output image format that scabbard generates. Supported values
   * are [OutputFormat.PNG] or [OutputFormat.SVG]
   *
   * @throws IllegalArgumentException when unsupported format is
   *     supplied.
   */
  open var outputFormat by mapCompilerProperty(
    jvmCompilerProperty = OUTPUT_FORMAT,
    valueMapper = OutputFormat::parse
  )

  /**
   * Executes the given [block] if the plugin is `enabled` with the
   * extension as the receiver.
   */
  fun ifEnabled(block: ScabbardPluginExtension.() -> Unit) {
    if (enabled) {
      block(this)
    }
  }
}
