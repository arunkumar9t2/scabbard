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

import dev.arunkumar.scabbard.gradle.options.JvmCompilerProperty
import dev.arunkumar.scabbard.gradle.options.applyCompilerProperty
import dev.arunkumar.scabbard.gradle.processor.manageScabbardProcessor
import dev.arunkumar.scabbard.gradle.util.SCABBARD
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * A gradle plugin that configures the project for Dagger 2 graph
 * visualization generation during build.
 *
 * Usage: The plugin can be configured by adding the following extension
 * block.
 *
 * ```
 * scabbard {
 *    enabled = true
 * }
 * ```
 *
 * How it works:
 * - It works by adding Scabbard's annotation processor to your
 *   project's dependencies in either `annotationProcessor`
 *   or `kapt` configuration based on the project type.
 * - Enables configuring the annotation processor behavior by delegating
 *   user defined values received via [ScabbardPluginExtension]
 *   to [JavaCompile] or [KotlinCompile] tasks
 * - The plugin does not add any new tasks to the project.
 *
 * Most of the work happens lazily by confirming to task configuration
 * avoidance.
 *
 * Results: After a successful build, the generated dagger visualization
 * images should be present in
 * - Java : `build/classes/java/$sourceSet/scabbard`
 * - Kotlin: `build/tmp/kapt3/classes/$sourceSet/scabbard`
 */
class ScabbardGradlePlugin : Plugin<Project> {

  private lateinit var scabbardPluginExtension: ScabbardPluginExtension

  override fun apply(project: Project) {
    scabbardPluginExtension = project.extensions.create(
      SCABBARD,
      ScabbardPluginExtension::class.java,
      project,
      Action<Boolean> { project.onScabbardStatusChanged() },
      Action<JvmCompilerProperty<*>> { onCompilerPropertySet(this) }
    )
  }

  /**
   * Callback to configure Scabbard's annotation processor dependency
   * when gradle configures [ScabbardPluginExtension]
   */
  private fun Project.onScabbardStatusChanged() {
    setupProjects {
      manageScabbardProcessor(enabled = scabbardPluginExtension.enabled)
    }
  }

  /**
   * Applies the given [jvmCompilerProperty] to all projects defined by
   * [setupProjects] based on whether the plugin is enabled or not.
   */
  private fun onCompilerPropertySet(jvmCompilerProperty: JvmCompilerProperty<*>) {
    scabbardPluginExtension.ifEnabled {
      project.setupProjects {
        applyCompilerProperty(jvmCompilerProperty)
      }
    }
  }

  /**
   * Simple extension to handle setting up different project modules
   * based on where the plugin is applied. If it is a root project, all
   * the subprojects are automatically configured.
   */
  private fun Project.setupProjects(block: Project.() -> Unit) {
    block(this)
    if (this == rootProject) {
      subprojects(block)
    }
  }
}
