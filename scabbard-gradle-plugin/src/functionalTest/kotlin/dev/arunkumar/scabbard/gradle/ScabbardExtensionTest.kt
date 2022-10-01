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

import com.google.common.truth.Truth.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Test

class ScabbardExtensionTest : ScabbardFunctionalTest() {

  @Test
  fun `assert scabbard extension is available`() {
    projectFile(
      path = "build.gradle",
      content = """
              | plugins {
              |   id "scabbard.gradle"
              | }
              | 
              | scabbard {
              |    enabled true
              |    failOnError true
              |    fullBindingGraphValidation true
              |    qualifiedNames false
              |    outputFormat "svg"
              | }
      """.trimMargin()
    )
    val result = gradleRunner
      .withArguments(":help")
      .build()
    assertThat(result.task(":help")?.outcome == TaskOutcome.SUCCESS)
  }
}
