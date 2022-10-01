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

package common

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import gradle.ConfigurablePlugin
import gradle.deps
import gradle.version
import kotlinx.validation.ApiValidationExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.dokka.gradle.DokkaPlugin
import com.github.benmanes.gradle.versions.VersionsPlugin as DependencyUpdatesPlugin

/**
 * Common build plugin that should be applied to root `build.gradle`
 * file. This plugin can be used to add logic that is meant to be added
 * to all subprojects in the current build.
 *
 * Example
 *
 * ```kotlin
 *  plugins {
 *    id "build-common"
 *  }
 * ```
 *
 * Note: To limit cross configuration, only logic that absolutely need
 * to exist such as linting and similar configuration should be added
 * here. For domain specific build logic, prefer to create dedicated
 * plugins and apply them using `plugins {}` block.
 *
 * Ideally we would like to cross configuration all together but it is
 * still convenient when we need to configure all projects at a single
 * place. If Gradle is evalue root build.gradle differently then it
 * would be best of both worlds.
 */
public class BuildCommonPlugin : ConfigurablePlugin({
  if (this != rootProject) {
    error("build-common should be only applied to root project")
  }
  configureDokka()

  configureApiValidation()

  configureSpotless()
  subprojects {
    configureSpotless()
  }

  apply<DependencyUpdatesPlugin>()
})

private fun Project.configureApiValidation() {
  apply(plugin = "org.jetbrains.kotlinx.binary-compatibility-validator")
  configure<ApiValidationExtension> {
    if (name != "scabbard-gradle-plugin") {
      ignoredProjects.addAll(
        listOf(
          "android-kotlin",
          "android-kotlin-hilt",
          "kotlin-anvil"
        )
      )
    }
  }
}

/** Configures spotless plugin on given subproject. */
private fun Project.configureSpotless() {
  apply<SpotlessPlugin>()
  configure<SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      targetExclude("$buildDir/**/*.kt", "bin/**/*.kt", "**/Version.kt")

      ktlint(deps.version("ktlint"))
        .setUseExperimental(true)
        .editorConfigOverride(
          mapOf(
            "indent_size" to "2",
            "continuation_indent_size" to "2",
            "disabled_rules" to "no-wildcard-imports,filename"
          )
        )
    }
  }
}

private fun Project.configureDokka() {
  apply<DokkaPlugin>()
}
