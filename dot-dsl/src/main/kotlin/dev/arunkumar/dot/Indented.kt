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
