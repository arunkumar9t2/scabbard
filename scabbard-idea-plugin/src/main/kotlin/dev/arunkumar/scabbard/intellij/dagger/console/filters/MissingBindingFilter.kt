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

package dev.arunkumar.scabbard.intellij.dagger.console.filters

import com.intellij.execution.filters.Filter
import com.intellij.openapi.project.Project

/**
 * [Filter] to create links to Dagger component's full dependency graph
 * by parsing Dagger's MissingBinding error on the console.
 */
class MissingBindingFilter(
  private val project: Project,
  private val missingBindingComponentExtractor: MissingBindingComponentExtractor = DefaultMissingBindingComponentExtractor(),
  private val linkExtractor: LinkResultExtractor = DefaultLinkExtractor(project)
) : Filter {
  override fun applyFilter(line: String, entireLength: Int): Filter.Result? {
    val components = missingBindingComponentExtractor.extract(line, entireLength)
    return if (components.isNotEmpty()) {
      linkExtractor.extract(components.first())
    } else null
  }
}
