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

package dev.arunkumar.scabbard.gradle.processor

import dev.arunkumar.scabbard.gradle.util.*
import dev.arunkumar.scabbard.gradle.util.ANDROID_APP_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.util.SCABBARD_GROUP
import dev.arunkumar.scabbard.gradle.util.SCABBARD_NAME
import dev.arunkumar.scabbard.gradle.util.SCABBARD_PROCESSOR_DEPENDENCY
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.plugins.PluginManager

internal const val KAPT_CONFIG = "kapt"
internal const val ANNOTATION_PROCESSOR_CONFIG = "annotationProcessor"

internal const val DAGGER_GROUP = "com.google.dagger"
internal const val DAGGER_COMPILER = "dagger-compiler"

internal const val DAGGER_HILT_ANDROID_COMPILER = "hilt-android-compiler"
internal const val DAGGER_HILT_COMPILER = "hilt-compiler"

/**
 * @return `true` if the [Dependency] is Scabbard's annotation processor
 *     dependency
 */
internal val Dependency.isScabbard get() = group == SCABBARD_GROUP && name == SCABBARD_NAME

/**
 * @return `true` if the [Dependency] is Dagger's main annotation
 *     processor dependency or hilt's compiler dependency
 */
internal val Dependency.isDaggerCompiler: Boolean
  get() = group == DAGGER_GROUP && (
    name == DAGGER_COMPILER ||
      name == DAGGER_HILT_ANDROID_COMPILER ||
      name == DAGGER_HILT_COMPILER
    )

/** TODO(arun) Make this Android variant aware */
internal fun apConfigName(isKapt: Boolean) = when {
  isKapt -> KAPT_CONFIG
  else -> ANNOTATION_PROCESSOR_CONFIG
}

/**
 * Removes the Scabbard annotation processor dependency from this
 * project. [isKapt] controls which config to remove from.
 *
 * @param isKapt when true the dependency is removed from `kapt`, if not
 *     it is removed from `annotationProcessor`
 */
private fun Project.removeScabbard(isKapt: Boolean = false) {
  configurations.findByName(apConfigName(isKapt))?.withDependencies {
    if (removeIf(Dependency::isScabbard)) {
      logger.info("Removed Scabbard from project:$path")
    }
  }
}

/**
 * Add scabbard processor dependency to the project in either
 * `annotationProcessor` or `kapt` configuration as determined by
 * [isKapt]
 *
 * The dependency is added only if the configuration itself is already
 * available, if not it is not added.
 */
private fun Project.addScabbard(isKapt: Boolean = false) {
  val configName = apConfigName(isKapt)
  configurations.findByName(configName)?.withDependencies {
    if (none(Dependency::isScabbard) && any(Dependency::isDaggerCompiler)) {
      logger.info("Adding scabbard to $configName configuration")
      dependencies.add(configName, SCABBARD_PROCESSOR_DEPENDENCY)
    }
  }
}

/**
 * Responsible for adding Scabbard's annotation processor to the Project
 * based on project properties such as if it is as `java`, `kotlin`,
 * `android` or a mixed project.
 *
 * The logic relies on callback's from Gradle's [PluginManager] API
 * to determine the course of action to avoid reliance on plugin
 * application orders. Relying on plugin application order can have
 * issue when using `subprojects` or `allprojects`
 *
 * The dependency is added without forcing any resolution of other
 * dependencies.
 *
 * @param enabled control whether the processor to be added to this
 *     project. If `false` any dependency on Scabbard's
 *     annotation processor will be removed from this
 *     project. Even if it was not added by this plugin.
 */
internal fun Project.manageScabbardProcessor(enabled: Boolean) {
  if (enabled) {
    pluginManager.run {
      withPlugin(ANDROID_APP_PLUGIN_ID) { addScabbard() }
      withPlugin(ANDROID_LIBRARY_PLUGIN_ID) { addScabbard() }
      withPlugin(JAVA_PLUGIN_ID) { addScabbard() }
      withPlugin(JAVA_LIBRARY_PLUGIN_ID) { addScabbard() }
      withPlugin(KAPT_PLUGIN_ID) {
        removeScabbard() // Remove from annotationProcessor and add to kapt alone.
        addScabbard(isKapt = true)
      }
    }
  } else {
    // Even though by default we don't add the annotation processor dependency, it is possible the
    // dep was added by other means. Hence manually remove the dependency if it is found.
    removeScabbard()
    removeScabbard(isKapt = true)
  }
}
