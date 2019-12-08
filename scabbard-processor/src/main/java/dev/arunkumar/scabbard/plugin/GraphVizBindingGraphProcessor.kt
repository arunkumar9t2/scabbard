package dev.arunkumar.scabbard.plugin

import com.squareup.javapoet.ClassName
import dagger.model.Binding
import dagger.model.BindingGraph
import dev.arunkumar.graphviz.dsl.*
import dev.arunkumar.scabbard.plugin.util.component1
import dev.arunkumar.scabbard.plugin.util.component2
import guru.nidi.graphviz.attribute.Color
import guru.nidi.graphviz.attribute.Rank
import guru.nidi.graphviz.attribute.Shape
import guru.nidi.graphviz.attribute.Style
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.model.MutableNode
import java.util.*
import javax.annotation.processing.Filer
import javax.inject.Inject
import javax.tools.StandardLocation
import kotlin.collections.HashMap

@ProcessorScope
class GraphVizBindingGraphProcessor
@Inject
constructor(
    private val bindingGraph: BindingGraph,
    private val filer: Filer
) : BindingGraphProcessor {

    override fun process() {
        val network = bindingGraph.network()
        val nodes = network.nodes()
        val edges = network.edges()

        val nodeIds = HashMap<BindingGraph.Node, UUID>()
        fun BindingGraph.Node.id() = nodeIds.computeIfAbsent(this) { UUID.randomUUID() }.toString()

        nodes
            .asSequence()
            .groupBy { it.componentPath() }
            .forEach { (component, nodes) ->
                val currentComponent = component.currentComponent()
                val componentName = ClassName.get(currentComponent)

                val outputFile = filer.createResource(
                    StandardLocation.CLASS_OUTPUT,
                    componentName.toString(),
                    componentName.simpleNames().joinToString("_").plus(".png")
                )

                val builder = mutGraph(name = currentComponent.toString(), directed = true) {
                    graphAttr {
                        add(Rank.dir(Rank.RankDir.LEFT_TO_RIGHT))
                        add("labeljust", "l")
                        add("label" to name())
                        add("compound" to true)
                    }
                    nodeAttr {
                        add(Shape.RECTANGLE)
                        add(Style.FILLED)
                        add(Color.TURQUOISE)
                    }

                    // Cache all added nodes for edge linking later
                    val nodesCache = HashMap<String, MutableNode>()
                    // Render all nodes
                    nodes.forEach { node ->
                        when (node) {
                            is Binding -> {
                                mutableNode(node.id()) {
                                    add("label" to node.key().toString())
                                    nodesCache[node.id()] = this
                                }
                            }
                            else -> {
                                println("${node.componentPath()} ${node.javaClass.name}")
                            }
                        }
                    }
                    // Render edges between nodes
                    edges.forEach { edge ->
                        val (source, target) = bindingGraph.network().incidentNodes(edge)
                        if (edge is BindingGraph.DependencyEdge) {
                            val sourceGraphNode = nodesCache[source.id()]
                            val targetGraphNode = nodesCache[target.id()]
                            if (sourceGraphNode != null && targetGraphNode != null) {
                                sourceGraphNode.addLink(targetGraphNode)
                            }
                        }
                    }
                }

                Graphviz.fromGraph(builder)
                    .render(Format.PNG)
                    .toOutputStream(outputFile.openOutputStream())
            }
    }
}