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

package kt

import kotlinx.validation.sourceSets
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.kotlinCommon() {
  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = "1.8"
      freeCompilerArgs = freeCompilerArgs + listOf(
        "-Xopt-in=kotlin.ExperimentalStdlibApi",
        "-Xopt-in=kotlin.RequiresOptIn",
        "-Xopt-in=kotlin.time.ExperimentalTime",
        "-Xopt-in=kotlin.experimental.ExperimentalTypeInference",
        "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        // "-Xexplicit-api=strict" // TODO Uncomment if strict API is needed
      )
    }
  }

  val onKaptStatusChanged = Action<Boolean> {
    if (this) {
      apply(plugin = "org.jetbrains.kotlin.kapt")
      sourceSets.configureEach {
        if (listOf("main", "test").contains(name)) {
          java {
            srcDirs("build/generated/source/kapt/$name")
          }
        }
      }
    }
  }

  extensions.create<KtExtension>("kt", onKaptStatusChanged)
}
