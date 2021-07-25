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

package dev.arunkumar.dot

import java.io.PrintWriter
import java.util.*

class DotGraph(private val header: String) : Indented() {
  private val elements = ArrayList<Indented>()

  fun add(element: Indented) {
    elements.add(element)
  }

  override fun write(level: Int, writer: PrintWriter) {
    indent(level, writer)
    writer.println("$header {")
    for (element in elements) {
      element.write(level + 1, writer)
    }
    indent(level, writer)
    writer.println("}")
  }
}
