package dev.arunkumar.scabbard.plugin.graphviz

import com.squareup.javapoet.ClassName
import dagger.model.Binding
import dagger.model.BindingGraph
import dagger.model.BindingGraph.DependencyEdge
import dagger.model.BindingKind.*
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.dot.dsl.directedGraph
import dev.arunkumar.scabbard.plugin.BindingGraphProcessor
import dev.arunkumar.scabbard.plugin.di.ProcessorScope
import dev.arunkumar.scabbard.plugin.util.component1
import dev.arunkumar.scabbard.plugin.util.component2
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
            var name = key().toString()
            val scopeName = scopeName()
            val isSubComponentCreator = kind() == SUBCOMPONENT_CREATOR
            val isMultibinding = kind().isMultibinding
            val newLine = "\\n"

            val isDelegate = kind() == DELEGATE
            if (isDelegate) {
                name = key().multibindingContributionIdentifier().get()
                    .let { it.module().split(".").last() + "." + it.bindingElement() }
            }

            buildString {
                scopeName?.let {
                    append(scopeName)
                    append(newLine)
                }
                append(name)
                if (isSubComponentCreator) {
                    append(newLine)
                    append("Subcomponent Creator")
                }
                if (isMultibinding) {
                    append(newLine)
                    @Suppress("NON_EXHAUSTIVE_WHEN")
                    when (kind()) {
                        MULTIBOUND_MAP -> append("MAP")
                        MULTIBOUND_SET -> append("SET")
                    }
                }
            }
        }
        else -> componentPath().toString()
    }

    override fun process() {
        val network = bindingGraph.network()
        val nodes = network.nodes()
        val edges = network.edges()
        try {
            nodes.asSequence()
                .groupBy { it.componentPath() }
                .forEach { (component, nodes) ->
                    val currentComponent = component.currentComponent()

                    val (outputFile, dotFile) = createOutputFiles(currentComponent)

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
                            addMultibindings(nodes)
                        }

                        // Render edges between all nodes
                        edges.forEach { edge ->
                            val (source, target) = bindingGraph.network().incidentNodes(edge)
                            addEdge(edge, source, target)
                        }
                    }.let { dotGraph ->
                        val dotCode = dotGraph.toString()

                        Graphviz.fromString(dotCode)
                            .scale(1.2)
                            .render(Format.PNG)
                            .toOutputStream(outputFile.openOutputStream())

                        dotFile.openOutputStream().write(dotCode.toByteArray())
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createOutputFiles(currentComponent: TypeElement): Pair<FileObject, FileObject> {
        val componentName = ClassName.get(currentComponent)
        val fileName = componentName.simpleNames().joinToString("_")
        return filer.createResource(
            CLASS_OUTPUT,
            componentName.toString(),
            fileName.plus(".png")
        ) to filer.createResource(
            CLASS_OUTPUT,
            componentName.toString(),
            fileName.plus(".dot")
        )
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
        if (node.kind().isMultibinding) return // Multi binding rendered as another cluster
        node.id {
            "label" eq node.label()
            "color" eq node.color
        }
    }

    private fun DotGraphBuilder.addMultibindings(currentComponentNodes: List<BindingGraph.Node>) {
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

    private fun DotGraphBuilder.addEdge(
        edge: BindingGraph.Edge,
        source: BindingGraph.Node,
        target: BindingGraph.Node
    ) {
        if (!nodeIds.containsKey(source)) return
        if (!nodeIds.containsKey(target)) return

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