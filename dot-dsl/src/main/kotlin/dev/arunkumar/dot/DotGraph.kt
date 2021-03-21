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
