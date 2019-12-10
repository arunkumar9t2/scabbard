package dev.arunkumar.dot

import dev.arunkumar.dot.EdgeOp.DIRECTED
import dev.arunkumar.dot.EdgeOp.UNDIRECTED
import java.util.regex.Matcher.quoteReplacement

fun Any.quote() = '"' + toString().replace("\"".toRegex(), quoteReplacement("\\\"")) + '"'

class DotNode(nodeName: Any) : DotStatement(nodeName.quote())

enum class EdgeOp(val op: String) {
    UNDIRECTED(" -- "),
    DIRECTED(" -> ")
}

open class DotEdge(
    open val leftNode: Any,
    open val rightNode: Any,
    private val edgeOp: EdgeOp = UNDIRECTED
) : DotStatement(leftNode.quote() + edgeOp.op + rightNode.quote())

class DirectedDotEdge(
    override val leftNode: Any,
    override val rightNode: Any
) : DotEdge(leftNode, rightNode, DIRECTED)