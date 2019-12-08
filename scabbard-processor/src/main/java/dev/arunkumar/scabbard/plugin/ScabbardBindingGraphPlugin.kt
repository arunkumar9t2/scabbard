package dev.arunkumar.scabbard.plugin

import com.google.auto.service.AutoService
import com.squareup.javapoet.ClassName
import dagger.model.Binding
import dagger.model.BindingGraph
import dagger.spi.BindingGraphPlugin
import dagger.spi.DiagnosticReporter
import dev.arunkumar.graphviz.dsl.add
import dev.arunkumar.graphviz.dsl.attr
import dev.arunkumar.graphviz.dsl.mutGraph
import dev.arunkumar.graphviz.dsl.mutableNode
import dev.arunkumar.scabbard.plugin.util.component1
import dev.arunkumar.scabbard.plugin.util.component2
import guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT
import guru.nidi.graphviz.attribute.Rank.dir
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import java.util.*
import java.util.UUID.randomUUID
import javax.annotation.processing.Filer
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.StandardLocation
import kotlin.collections.HashMap

@AutoService(BindingGraphPlugin::class)
class ScabbardBindingGraphPlugin : BindingGraphPlugin {

    private lateinit var filer: Filer
    private lateinit var types: Types
    private lateinit var elements: Elements

    override fun pluginName() = "ScabbardBindingGraphPlugin"

    override fun initFiler(filer: Filer) {
        this.filer = filer
    }

    override fun initTypes(types: Types) {
        this.types = types
    }

    override fun initElements(elements: Elements) {
        this.elements = elements
    }

    override fun visitGraph(bindingGraph: BindingGraph, diagnosticReporter: DiagnosticReporter) {
        val network = bindingGraph.network()
        val nodes = network.nodes()
        val edges = network.edges()

        val nodeIds = HashMap<BindingGraph.Node, UUID>()
        fun BindingGraph.Node.id() = nodeIds.computeIfAbsent(this) { randomUUID() }.toString()
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
                    attr {
                        add(dir(LEFT_TO_RIGHT))
                        add("labeljust", "l")
                        add("label" to name())
                        add("compound" to true)
                    }
                    // Render all nodes
                    nodes.forEach { node ->
                        when (node) {
                            is Binding -> {
                                mutableNode(node.id()) {
                                    add("label" to node.key().toString())
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
                            if (!edge.isEntryPoint) {
                                mutableNode(source.id()) {
                                    add("label" to source.toString())
                                    addLink(target.id())
                                }
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

