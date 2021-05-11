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
