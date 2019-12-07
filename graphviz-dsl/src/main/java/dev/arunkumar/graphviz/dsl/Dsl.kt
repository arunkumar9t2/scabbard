@file:Suppress("NOTHING_TO_INLINE")

package dev.arunkumar.graphviz.dsl

import guru.nidi.graphviz.attribute.*
import guru.nidi.graphviz.model.Factory
import guru.nidi.graphviz.model.MutableAttributed
import guru.nidi.graphviz.model.MutableGraph
import guru.nidi.graphviz.model.MutableNode

inline fun mutGraph(
    name: String = "",
    strict: Boolean = false,
    directed: Boolean = false,
    cluster: Boolean = false,
    crossinline builder: MutableGraph.() -> Unit = { }
): MutableGraph = Factory.mutGraph(name).apply {
    isStrict = strict
    isDirected = directed
    isCluster = cluster
}.apply(builder)

inline fun MutableGraph.mutableGraph(
    name: String = "",
    strict: Boolean = false,
    directed: Boolean = false,
    cluster: Boolean = false,
    crossinline builder: MutableGraph.() -> Unit = { }
): MutableGraph = mutGraph(name, strict, directed, cluster, builder).also { it.addTo(this) }

inline fun MutableGraph.attr(builder: MutableAttributed<MutableGraph, ForGraph>.() -> Unit) =
    graphAttrs().builder()

inline fun MutableGraph.nodeAttr(builder: MutableAttributed<MutableGraph, ForNode>.() -> Unit) =
    nodeAttrs().builder()

inline fun MutableGraph.mutableNode(
    name: String = "",
    builder: MutableNode.() -> Unit = {}
): MutableNode = Factory.mutNode(name).also { add(it) }.apply(builder)


inline fun MutableNode.color(color: Color) {
    add(color)
}

inline fun MutableNode.shape(shape: Shape) {
    add(shape)
}

inline fun MutableNode.style(style: Style) {
    add(style)
}

inline fun <T, F : For> MutableAttributed<T, F>.add(attributePair: Pair<String, Any>) {
    add(attributePair.first, attributePair.second)
}
