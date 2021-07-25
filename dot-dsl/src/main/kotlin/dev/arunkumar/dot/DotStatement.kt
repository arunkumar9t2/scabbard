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

@file:Suppress("NOTHING_TO_INLINE")

package dev.arunkumar.dot

import java.io.PrintWriter
import java.util.regex.Matcher

fun Any.quote() = '"' + toString().replace("\"".toRegex(), Matcher.quoteReplacement("\\\"")) + '"'

open class DotStatement(
  protected val base: String
) : Indented() {

  protected val attributes: LinkedHashMap<String, Any> = LinkedHashMap()

  fun addAttribute(name: String, value: Any) {
    attributes[name] = value
  }

  infix fun String.`=`(value: Any) {
    attributes[this] = value
  }

  fun addAttributeFormat(name: String, format: String, vararg args: Any) {
    addAttribute(name, String.format(format, *args))
  }

  override fun write(level: Int, writer: PrintWriter) {
    indent(level, writer)
    writer.print(base)
    if (attributes.isNotEmpty()) {
      writer.print(
        attributes
          .entries
          .joinToString(
            separator = ", ",
            prefix = " [",
            postfix = "]"
          ) { (key, value) -> "$key=${value.quote()}" }
      )
    }
    writer.println()
  }
}

class DotNode(nodeName: Any) : DotStatement(nodeName.quote())
