package dev.arunkumar.dot

import java.util.regex.Matcher.quoteReplacement

fun Any.quote() = '"' + toString().replace("\"".toRegex(), quoteReplacement("\\\"")) + '"'

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