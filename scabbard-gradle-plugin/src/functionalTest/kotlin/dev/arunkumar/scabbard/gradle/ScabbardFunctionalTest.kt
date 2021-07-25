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

import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File

abstract class ScabbardFunctionalTest {
  @get:Rule
  val testProjectDir = TemporaryFolder()

  protected lateinit var gradleRunner: GradleRunner

  @Before
  fun setUp() {
    gradleRunner = GradleRunner.create()
      .withProjectDir(testProjectDir.root)
      .withPluginClasspath()
  }

  protected fun projectFile(path: String, content: String): File {
    val root = testProjectDir.root
    return File(root, path).apply {
      parentFile?.let { if (!it.exists()) it.mkdirs() }
      if (exists()) delete()
      createNewFile()
      writeText(content)
    }
  }

  protected fun addSimpleKtSourceFile() {
    projectFile(
      path = "/src/main/java/scabbard/main.kt",
      content = """
              package scabbard
              import dagger.Component
              import javax.inject.Inject
              
              class Node @Inject constructor()
              
              @Component
              interface SimpleComponent {
                fun node(): Node
              }
      """.trimIndent()
    )
  }

  protected fun addSimpleJavaSourceFile() {
    projectFile(
      path = "/src/main/java/scabbard/ScabbardSample.java",
      content = """
              package scabbard;
              import javax.inject.Inject;
              import dagger.Component;
              
              public class ScabbardSample {
                static class Hello {
                    @Inject Hello() {}
                }
                @Component
                interface SimpleComponent {
                  Hello hello();
                }
              }
      """.trimIndent()
    )
  }
}
