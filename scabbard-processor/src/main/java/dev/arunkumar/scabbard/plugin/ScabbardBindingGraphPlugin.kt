package dev.arunkumar.scabbard.plugin

import com.google.auto.service.AutoService
import dagger.model.BindingGraph
import dagger.spi.BindingGraphPlugin
import dagger.spi.DiagnosticReporter
import guru.nidi.graphviz.*
import guru.nidi.graphviz.attribute.Arrow
import guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT
import guru.nidi.graphviz.attribute.Rank.dir
import guru.nidi.graphviz.engine.Format
import javax.annotation.processing.Filer
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.StandardLocation

@AutoService(BindingGraphPlugin::class)
class ScabbardBindingGraphPlugin : BindingGraphPlugin {

    private lateinit var filer: Filer
    private lateinit var types: Types
    private lateinit var elements: Elements

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
        network.nodes()
            .asSequence()
            .groupBy { it.componentPath() }
            .forEach { (component, nodes) ->
                val outputFile = filer.createResource(
                    StandardLocation.CLASS_OUTPUT,
                    "",
                    component.currentComponent().qualifiedName.toString().replace(".", "_") + ".png"
                )

                graph(directed = true) {
                    edge["color" eq "red", Arrow.TEE]
                    graph[dir(LEFT_TO_RIGHT)]
                    nodes.reduce { acc, node ->
                        acc.componentPath().currentComponent().qualifiedName.toString() - node.componentPath().currentComponent().qualifiedName.toString()
                        node
                    }
                }.toGraphviz()
                    .render(Format.PNG)
                    .toOutputStream(outputFile.openOutputStream())
            }
    }
}