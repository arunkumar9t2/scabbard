package dev.arunkumar.dot.dsl

import dev.arunkumar.dot.*

@DslMarker
annotation class DotDslScope

@DotDslScope
class DotGraphBuilder(val dotGraph: DotGraph) {

    inline fun graphAttributes(builder: DotStatement.() -> Unit) {
        dotGraph.add(DotStatement("graph").apply(builder))
    }

    inline fun nodeAttributes(builder: DotStatement.() -> Unit) {
        dotGraph.add(DotStatement("node").apply(builder))
    }

    inline fun subgraph(name: String, graphBuilder: DotGraphBuilder.() -> Unit) {
        val subgraph = DotGraphBuilder(DotGraph("subgraph $name")).apply(graphBuilder).dotGraph
        dotGraph.add(subgraph)
    }

    inline fun cluster(name: String, graphBuilder: DotGraphBuilder.() -> Unit) {
        subgraph("cluster_$name", graphBuilder)
    }

    inline operator fun String.invoke(nodeBuilder: DotNode.() -> Unit = {}) {
        dotGraph.add(DotNode(this).apply(nodeBuilder))
    }

    inline fun nodes(vararg nodes: String, nodeBuilder: DotNode.() -> Unit = {}) {
        nodes.forEach { it.invoke(nodeBuilder) }
    }

    infix fun String.link(target: String): EdgeBuilder {
        val dotEdge = DirectedDotEdge(this, target).also(dotGraph::add)
        return EdgeBuilder(dotEdge)
    }

    inline fun edge(left: String, right: String, edgeBuilder: DotEdge.() -> Unit) {
        dotGraph.add(DirectedDotEdge(left, right).apply(edgeBuilder))
    }
}

@DotDslScope
class EdgeBuilder(private val dotEdge: DotEdge) {
    operator fun invoke(edgeBuilder: DotEdge.() -> Unit) {
        dotEdge.apply(edgeBuilder)
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
                "fillcolor" eq "black"
            }

            nodes("A", "B", "C")
        }

        cluster("World") {

            nodes("D", "E", "F")
        }

        ("A" link "B") {
            "style" eq "dashed"
            "taillabel" eq "subcomponent"
        }
        "E" link "C"
        "A" link "C"
    }

    println(dotGraph.toString())
}