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

package dev.arunkumar.scabbard.intellij.dagger.console.filters

interface MissingBindingComponentExtractor {
  fun extract(line: String, entireLength: Int): Set<DaggerComponent>
}

class DefaultMissingBindingComponentExtractor : MissingBindingComponentExtractor {
  companion object {
    private val DAGGER_ERROR_FORMAT = "error: \\[Dagger/[a-zA-Z]+]".toRegex()
    private const val EXTENDS = "extends"
    private const val IMPLEMENTS = "implements"
  }

  private var isDaggerLog = false

  override fun extract(line: String, entireLength: Int): Set<DaggerComponent> {
    if (line.contains(DAGGER_ERROR_FORMAT)) {
      isDaggerLog = true
      return emptySet()
    }
    if (isDaggerLog) {
      val lineStart = entireLength - line.length
      // Parse the component simple name from dagger component error line
      if (line.trim().endsWith("{") || line.trim().endsWith(",")) {
        val classNameWithModifiers = when {
          line.contains(EXTENDS) -> line.split(EXTENDS).first()
          line.contains(IMPLEMENTS) -> line.split(IMPLEMENTS).first()
          else -> line.split("{").first()
        }
        // parse class name from string such as "public abstract class AppComponent"
        classNameWithModifiers
          .split(" ")
          .map { it.trim() }
          .lastOrNull { it.isNotBlank() }
          ?.let { componentName ->
            val start = line.indexOf(componentName) + lineStart
            val end = start + componentName.length
            isDaggerLog = false
            return setOf(DaggerComponent(start, end, componentName))
          }
      }
    }
    return emptySet()
  }
}
