package dev.arunkumar.dot.dsl

import dev.arunkumar.dot.DotGraph
import dev.arunkumar.dot.DotStatement
import java.io.PrintWriter
import java.io.StringWriter

@DslMarker
annotation class DotDslScope

@DotDslScope
class DotGraphBuilder(val dotGraph: DotGraph) {

    inline fun graphAttributes(builder: DotStatement.() -> Unit) {
        dotGraph.add(DotStatement("graph").apply(builder))
    }

    inline fun nodeAttributes(builder: DotStatement.() -> Unit) {
        "node".invoke(builder)
    }

    inline fun subgraph(name: String, builder: DotGraphBuilder.() -> Unit) {
        val subgraph = DotGraphBuilder(DotGraph("subgraph $name")).apply(builder).dotGraph
        dotGraph.add(subgraph)
    }

    inline fun cluster(name: String, builder: DotGraphBuilder.() -> Unit) {
        subgraph("cluster_$name", builder)
    }

    inline operator fun String.invoke(builder: DotStatement.() -> Unit = {}) {
        dotGraph.add(DotStatement(this).apply(builder))
    }

    inline fun nodes(vararg nodes: String, builder: DotStatement.() -> Unit = {}) {
        nodes.forEach { it.invoke(builder) }
    }
}

fun directedGraph(
    label: String,
    builder: DotGraphBuilder.() -> Unit
) = DotGraphBuilder(DotGraph("digraph $label")).apply(builder).dotGraph

fun main(ars: Array<String>) {
    val dotGraph = directedGraph("Arun") {

        graphAttributes {
            "rankdir" eq "LR"
            "labeljust" eq "l"
            "compound" eq true
            "label" eq "Hello"
        }

        cluster("Hello") {

            graphAttributes {
                "rankdir" eq "LR"
                "color" eq "blue"
                "label" eq "Cluster One"
            }

            nodeAttributes {
                "style" eq "dashed"
                "fillcolor" eq "black"
            }

            nodes("A", "B", "C") {
                "label" eq "Grouped Node"
            }
        }

        cluster("World") {

            nodes("D", "E", "F") {
                "label" eq "Grouped Node"
            }
        }
    }

    val stringWriter = StringWriter()
    PrintWriter(stringWriter).let { dotGraph.write(0, it) }
    println(stringWriter.toString())
}