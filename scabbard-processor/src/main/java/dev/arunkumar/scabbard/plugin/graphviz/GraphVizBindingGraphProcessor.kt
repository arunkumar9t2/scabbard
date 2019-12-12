package dev.arunkumar.scabbard.plugin.graphviz

import com.squareup.javapoet.ClassName
import dagger.model.Binding
import dagger.model.BindingGraph
import dagger.model.BindingGraph.*
import dagger.model.BindingKind.DELEGATE
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.dot.dsl.directedGraphBuilder
import dev.arunkumar.scabbard.plugin.BindingGraphProcessor
import dev.arunkumar.scabbard.plugin.di.ProcessorScope
import dev.arunkumar.scabbard.plugin.options.ScabbardOptions
import dev.arunkumar.scabbard.plugin.util.component1
import dev.arunkumar.scabbard.plugin.util.component2
import dev.arunkumar.scabbard.plugin.util.tryCatchLogging
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import java.util.*
import javax.annotation.processing.Filer
import javax.inject.Inject
import javax.lang.model.element.TypeElement
import javax.tools.FileObject
import javax.tools.StandardLocation.CLASS_OUTPUT

@ProcessorScope
class GraphVizBindingGraphProcessor
@Inject
constructor(
    override val bindingGraph: BindingGraph,
    private val scabbardOptions: ScabbardOptions,
    private val filer: Filer
) : BindingGraphProcessor {

    private val scopeColorsCache = mutableMapOf("" to "turquoise")

    private val Binding.color
        get() = scopeColorsCache
            .computeIfAbsent(scopeName() ?: "") { SCOPE_COLORS.random() }

    private val Binding.isEntryPoint get() = bindingGraph.entryPointBindings().contains(this)

    private fun createOutputFiles(currentComponent: TypeElement): Pair<FileObject, FileObject> {
        val componentName = ClassName.get(currentComponent)
        val fileName = componentName.simpleNames().joinToString("_")
        return filer.createResource(
            CLASS_OUTPUT,
            componentName.toString(),
            "$fileName.png"
        ) to filer.createResource(
            CLASS_OUTPUT,
            componentName.toString(),
            "$fileName.dot"
        )
    }

    private val globalNodeIds = mutableMapOf<Node, String>()
    private val Node.id
        get() = globalNodeIds.computeIfAbsent(this) {
            UUID.randomUUID().toString()
        }

    override fun process() = tryCatchLogging {
        val network = bindingGraph.network()
        val nodes = network.nodes()
        val allEdges = network.edges()
        nodes.asSequence()
            .groupBy { it.componentPath() }
            .forEach { (component, componentNodes) ->
                val currentComponent = component.currentComponent()
                val dotGraphBuilder = buildGraph(
                    currentComponent,
                    componentNodes.asSequence(),
                    allEdges.asSequence() // TODO(arun) why pass global edges here?
                )
                val (outputFile, dotFile) = createOutputFiles(currentComponent)
                val dotOutput = dotGraphBuilder.dotGraph.toString()

                Graphviz.fromString(dotOutput)
                    .scale(1.2)
                    .render(Format.PNG)
                    .toOutputStream(outputFile.openOutputStream())
                dotFile.openOutputStream().write(dotOutput.toByteArray())
            }
    }

    private fun buildGraph(
        currentComponent: TypeElement,
        nodes: Sequence<Node>,
        edges: Sequence<Edge>
    ): DotGraphBuilder = directedGraphBuilder(currentComponent.toString()) {
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
                "labeljust" eq "l"
                "label" eq "Entry Points"
            }

            addEntryPoints(nodes)
        }

        cluster("Dependency Graph") {

            graphAttributes {
                "labeljust" eq "l"
                "label" eq "Dependency Graph"
            }

            // Add dependency graph
            nodes.forEach { node ->
                when (node) {
                    is Binding -> addDependencyNode(node)
                }
            }

            // Add multi bindings
            addMultiBindings(nodes)
        }

        // Render edges between all nodes
        edges.forEach { edge ->
            val (source, target) = bindingGraph.network().incidentNodes(edge)
            addEdge(edge, source, target)
        }
    }

    private fun DotGraphBuilder.addEntryPoints(nodes: Sequence<Node>) = nodes
        .filterIsInstance<Binding>()
        .filter { it.isEntryPoint }
        .forEach { node ->
            node.id {
                "shape" eq "component"
                "label" eq node.label()
            }
        }

    private fun DotGraphBuilder.addDependencyNode(node: Binding) {
        if (node.isEntryPoint) return // Entry points are rendered in another cluster
        if (node.kind().isMultibinding) return // Multi binding rendered as another cluster
        node.id {
            "label" eq node.label()
            "color" eq node.color
        }
    }

    private fun DotGraphBuilder.addMultiBindings(currentComponentNodes: Sequence<Node>) {
        currentComponentNodes.asSequence()
            .filterIsInstance<Binding>()
            .filter { it.kind().isMultibinding }
            .forEach { multiBinding ->
                val name = multiBinding.key().toString()
                cluster(name) {
                    graphAttributes {
                        "label" eq name
                        "labeljust" eq "c"
                        "style" eq "rounded"
                    }
                    multiBinding.id {
                        "shape" eq "tab"
                        "label" eq multiBinding.label()
                        "color" eq multiBinding.color
                    }
                    bindingGraph
                        .requestedBindings(multiBinding)
                        .forEach { binding -> addDependencyNode(binding) }
                }
            }
    }

    private fun DotGraphBuilder.addEdge(edge: Edge, source: Node, target: Node) {
        if (!globalNodeIds.containsKey(source)) return
        if (!globalNodeIds.containsKey(target)) return

        if (edge is DependencyEdge && !edge.isEntryPoint) {
            (source.id link target.id) {
                if ((source as? Binding)?.kind() == DELEGATE) {
                    // Delegate edges i.e usually using @Binds
                    "style" eq "dotted"
                    "label" eq "implements"
                }
            }
        }
    }
}