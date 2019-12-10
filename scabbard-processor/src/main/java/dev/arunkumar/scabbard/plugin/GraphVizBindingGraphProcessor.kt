package dev.arunkumar.scabbard.plugin

import com.squareup.javapoet.ClassName
import dagger.model.Binding
import dagger.model.BindingGraph
import dagger.model.BindingGraph.DependencyEdge
import dagger.model.BindingKind
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.dot.dsl.directedGraph
import dev.arunkumar.scabbard.plugin.util.component1
import dev.arunkumar.scabbard.plugin.util.component2
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import java.util.*
import javax.annotation.processing.Filer
import javax.inject.Inject
import javax.tools.StandardLocation

@ProcessorScope
class GraphVizBindingGraphProcessor
@Inject
constructor(
    private val bindingGraph: BindingGraph,
    private val filer: Filer
) : BindingGraphProcessor {

    private val scopeColors by lazy {
        listOf(
            "aquamarine",
            "blue",
            "cyan",
            "magenta",
            "tomato",
            "yellow",
            "deeppink",
            "gold",
            "crimson",
            "chocolate1"
        )
    }

    private val nodeIds = mutableMapOf<BindingGraph.Node, UUID>()
    private val BindingGraph.Node.id get() = nodeIds.computeIfAbsent(this) { UUID.randomUUID() }.toString()

    private val scopeColorCache = mutableMapOf("" to "turquoise")

    private fun Binding.scopeName() = when {
        scope().isPresent -> "@" + scope().get().scopeAnnotationElement().simpleName.toString()
        else -> null
    }

    private val Binding.color
        get() = scopeColorCache.computeIfAbsent(scopeName() ?: "") {
            scopeColors.random()
        }

    private val Binding.isEntryPoint get() = bindingGraph.entryPointBindings().contains(this)

    private fun BindingGraph.Node.label(): String = when (this) {
        is Binding -> {
            val scopeName = scopeName()
            val isSubComponentCreator = kind() == BindingKind.SUBCOMPONENT_CREATOR
            when {
                isSubComponentCreator -> "${key()}\\n\\nSubcomponent Creator"
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
        nodes.asSequence()
            .groupBy { it.componentPath() }
            .forEach { (component, nodes) ->
                val currentComponent = component.currentComponent()
                val componentName = ClassName.get(currentComponent)

                val outputFile = filer.createResource(
                    StandardLocation.CLASS_OUTPUT,
                    componentName.toString(),
                    componentName.simpleNames().joinToString("_").plus(".png")
                )

                directedGraph(currentComponent.toString()) {

                    graphAttributes {
                        "rankdir" eq "LR"
                        "labeljust" eq "l"
                        "label" eq currentComponent.toString()
                        "pad" eq 0.5
                        "compound" eq true
                    }

                    nodeAttributes {
                        "shape" eq "rectangle"
                        "style" eq "filled"
                        "color" eq "turquoise"
                    }

                    cluster("Entry Points") {

                        graphAttributes {
                            "dir" eq "forward"
                            "labeljust" eq "l"
                            "label" eq "Entry Points"
                            "compound" eq true
                        }

                        addEntryPoints(nodes)
                    }

                    cluster("Dependency Graph") {

                        graphAttributes {
                            "dir" eq "forward"
                            "labeljust" eq "l"
                            "label" eq "Dependency Graph"
                            "compound" eq true
                        }

                        // Add dependency graph
                        nodes.forEach { node ->
                            when (node) {
                                is Binding -> addDependencyNode(node)
                            }
                        }
                    }

                    // Render edges between all nodes
                    edges.forEach { edge ->
                        val (source, target) = bindingGraph.network().incidentNodes(edge)
                        if (edge is DependencyEdge) {
                            source.id link target.id
                        }
                    }
                }.let { dotGraph ->
                    Graphviz.fromString(dotGraph.toString())
                        .render(Format.PNG)
                        .toOutputStream(outputFile.openOutputStream())
                }
            }
    }

    private fun DotGraphBuilder.addEntryPoints(nodes: List<BindingGraph.Node>) = nodes.asSequence()
        .filterIsInstance<Binding>()
        .filter { it.isEntryPoint }
        .forEach { node ->
            node.id {
                "shape" eq "component"
                "label" eq node.label()
            }
        }

    private fun DotGraphBuilder.addDependencyNode(node: Binding) {
        if (node.isEntryPoint) return
        node.id {
            "label" eq node.label()
            "color" eq node.color
        }
    }
}