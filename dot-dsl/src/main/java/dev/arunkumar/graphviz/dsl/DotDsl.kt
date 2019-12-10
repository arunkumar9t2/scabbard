package dev.arunkumar.graphviz.dsl

import java.io.PrintWriter
import java.util.*
import java.util.regex.Matcher.quoteReplacement

abstract class Indented {

    internal abstract fun write(level: Int, writer: PrintWriter)

    internal fun indent(
        level: Int,
        writer: PrintWriter
    ): PrintWriter = writer.apply {
        print(" ".repeat(level * 2))
    }
}

fun Any.quote() = '"' + toString().replace("\"".toRegex(), quoteReplacement("\\\"")) + '"'

open class DotStatement(
    private val base: String,
    private val attributes: LinkedHashMap<String, Any> = LinkedHashMap()
) : Indented() {

    fun addAttribute(name: String, value: Any) {
        attributes[name] = value
    }

    fun addAttributeFormat(name: String, format: String, vararg args: Any) {
        addAttribute(name, String.format(format, *args))
    }

    override fun write(level: Int, writer: PrintWriter) {
        indent(level, writer)
        writer.print(base)
        if (attributes.isNotEmpty()) {
            attributes
                .entries
                .joinToString(
                    separator = ", ",
                    prefix = " [",
                    postfix = "]"
                ) { (key, value) -> "${key}=${value.quote()}" }
        }
        writer.println()
    }
}

class DotNode(nodeName: Any) : DotStatement(nodeName.quote())

open class DotEdge(
    open val leftNode: Any,
    open val rightNode: Any,
    private val edgeOp: String = " -- "
) : DotStatement(leftNode.quote() + edgeOp + rightNode.quote())

class DirectedDotEdge(
    override val leftNode: Any,
    override val rightNode: Any
) : DotEdge(leftNode, rightNode, " -> ")