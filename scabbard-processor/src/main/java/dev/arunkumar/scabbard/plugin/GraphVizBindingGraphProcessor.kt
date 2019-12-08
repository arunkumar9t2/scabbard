package dev.arunkumar.scabbard.plugin

import com.squareup.javapoet.ClassName
import dagger.model.Binding
import dagger.model.BindingGraph
import dagger.model.BindingGraph.DependencyEdge
import dev.arunkumar.graphviz.dsl.*
import dev.arunkumar.scabbard.plugin.util.component1
import dev.arunkumar.scabbard.plugin.util.component2
import guru.nidi.graphviz.attribute.Color
import guru.nidi.graphviz.attribute.Color.*
import guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT
import guru.nidi.graphviz.attribute.Rank.dir
import guru.nidi.graphviz.attribute.Shape.RECTANGLE
import guru.nidi.graphviz.attribute.Style.FILLED
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.model.MutableGraph
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

    private val scopeColors by lazy {
        listOf(
            AQUAMARINE,
            BLUE,
            CYAN,
            MAGENTA,
            TOMATO,
            YELLOW,
            DEEPPINK,
            GOLD,
            CRIMSON,
            CHOCOLATE1,
            CADETBLUE1
        )
    }

    private val nodeIds = HashMap<BindingGraph.Node, UUID>()
    private val BindingGraph.Node.id get() = nodeIds.computeIfAbsent(this) { UUID.randomUUID() }.toString()

    private val scopeColorCache = HashMap<String, Color>().apply {
        put("", TURQUOISE)
    }

    private fun Binding.scopeName() = if (scope().isPresent) {
        "@" + scope().get().scopeAnnotationElement().simpleName.toString()
    } else {
        null
    }

    private val Binding.color
        get() = scopeColorCache.computeIfAbsent(scopeName() ?: "") {
            scopeColors.random()
        }
    private val Binding.isEntryPoint get() = bindingGraph.entryPointBindings().contains(this)

    private fun BindingGraph.Node.label(): String = when (this) {
        is Binding -> {
            val scopeName = scopeName()
            when {
                scopeName != null -> "$scopeName\\n${key()}"
                else -> key().toString()
            }
        }
        else -> componentPath().toString()
    }

    override fun process() {
        val network = bindingGraph.network()
        val nodes = network.nodes()
        val edges = network.edges()

        val nodeIds = HashMap<BindingGraph.Node, UUID>()
        fun BindingGraph.Node.id() = nodeIds.computeIfAbsent(this) { UUID.randomUUID() }.toString()

        nodes.asSequence()
            .groupBy { it.componentPath() }
            .forEach { (component, nodes) ->
                // Cache all added nodes for edge linking later
                val nodesCache = HashMap<String, MutableNode>()

                val currentComponent = component.currentComponent()
                val componentName = ClassName.get(currentComponent)

                val outputFile = filer.createResource(
                    StandardLocation.CLASS_OUTPUT,
                    componentName.toString(),
                    componentName.simpleNames().joinToString("_").plus(".png")
                )

                val rootGraph = mutGraph(name = currentComponent.toString(), directed = true) {

                    graphAttr {
                        add(dir(LEFT_TO_RIGHT))
                        add("labeljust", "l")
                        add("label" to name())
                        add("compound" to true)
                    }
                    nodeAttr {
                        add(RECTANGLE)
                        add(FILLED)
                        add(TURQUOISE)
                    }

                    addEntryPoints(nodes, nodesCache)

                    mutableGraph(name = "Dependency Graph", cluster = true) {

                        graphAttr {
                            add(dir(LEFT_TO_RIGHT))
                            add("labeljust", "l")
                            add("label" to name())
                            add("compound" to true)
                        }

                        // Add dependency graph
                        nodes.forEach { node ->
                            when (node) {
                                is Binding -> addDependencyNode(node, nodesCache)
                            }
                        }
                        // Render edges between all nodes
                        edges.forEach { edge ->
                            val (source, target) = bindingGraph.network().incidentNodes(edge)
                            if (edge is DependencyEdge) {
                                val sourceGraphNode = nodesCache[source.id]
                                val targetGraphNode = nodesCache[target.id]
                                if (sourceGraphNode != null && targetGraphNode != null) {
                                    sourceGraphNode.addLink(targetGraphNode)
                                }
                            }
                        }
                    }
                }

                Graphviz.fromGraph(rootGraph)
                    .render(Format.PNG)
                    .toOutputStream(outputFile.openOutputStream())
            }
    }

    private fun MutableGraph.addEntryPoints(
        nodes: List<BindingGraph.Node>,
        cache: HashMap<String, MutableNode>
    ) = nodes.asSequence()
        .filterIsInstance<Binding>()
        .filter { it.isEntryPoint }
        .forEach { node ->
            val id = node.id
            cache[id] = mutableNode(id) {
                add("shape" to "component")
                add("label" to node.label())
                add(node.color)
            }
        }

    private fun MutableGraph.addDependencyNode(node: Binding, cache: HashMap<String, MutableNode>) {
        if (node.isEntryPoint) return

        val id = node.id
        cache[id] = mutableNode(id) {
            add("label" to node.label())
            add(node.color)
        }
    }
}