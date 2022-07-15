/*
 * Copyright 2021 Arunkumar
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

import dev.arunkumar.scabbard.gradle.compilerproperties.*
import dev.arunkumar.scabbard.gradle.compilerproperties.compilerProperty
import dev.arunkumar.scabbard.gradle.output.OutputFormat
import dev.arunkumar.scabbard.gradle.util.enabledProperty
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
 * @param onEnabledStatusChange The action to execute when
 *     [ScabbardPluginExtension.enabled] is configured
 * @param onCompilerPropertyChanged The action to execute when any of
 *     the compiler property is configured.
 */
open class ScabbardPluginExtension(
  val project: Project,
  internal val onEnabledStatusChange: Action<Boolean>,
  internal val onCompilerPropertyChanged: Action<CompilerProperty<*>>
) {

  /**
   * Control whether scabbard is enabled or not. Default value is
   * `false`.
   */
  open var enabled by enabledProperty()

  /**
   * By default, scabbard does not fail the build when any error occurs
   * in scabbard's processor. Setting this property to `true` will
   * change that behaviour to fail on any error for debugging purposes.
   */
  open var failOnError by compilerProperty(FAIL_ON_ERROR)

  /**
   * Flag to control if fully qualified names should be used everywhere
   * in the graph. Default value is `false`
   */
  open var qualifiedNames by compilerProperty(QUALIFIED_NAMES)

  /**
   * Configures Dagger processor to do full graph validation which
   * processes each `@Module`, `@Component` and `@Subcomponent`. This
   * enables visualization of missing bindings and generates graphs for
   * `@Module` too.
   *
   * @see [https://dagger.dev/compiler-options.html]
   */
  open var fullBindingGraphValidation by mapCompilerProperty(
    compilerProperty = FULL_GRAPH_VALIDATION,
    valueMapper = FULL_GRAPH_VALIDATION_MAPPER
  )

  /**
   * The output image format that scabbard generates. Supported values
   * are [OutputFormat.PNG] or [OutputFormat.SVG]
   *
   * @throws IllegalArgumentException when unsupported format is
   *     supplied.
   */
  open var outputFormat by mapCompilerProperty(
    compilerProperty = OUTPUT_FORMAT,
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
