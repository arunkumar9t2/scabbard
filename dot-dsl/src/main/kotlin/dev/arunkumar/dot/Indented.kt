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

package dev.arunkumar.dot

import java.io.PrintWriter
import java.io.StringWriter

abstract class Indented {

  internal abstract fun write(level: Int, writer: PrintWriter)

  internal fun indent(
    level: Int,
    writer: PrintWriter
  ): PrintWriter {
    writer.print(" ".repeat(level * 2))
    return writer
  }

  override fun toString(): String {
    return StringWriter().also { write(0, PrintWriter(it)) }.toString()
  }
}
