package dev.arunkumar.scabbard.plugin

import com.google.auto.service.AutoService
import com.google.common.base.Joiner
import com.squareup.javapoet.ClassName
import dagger.model.BindingGraph
import dagger.spi.BindingGraphPlugin
import dagger.spi.DiagnosticReporter
import dev.arunkumar.graphviz.dsl.add
import dev.arunkumar.graphviz.dsl.attr
import dev.arunkumar.graphviz.dsl.mutGraph
import dev.arunkumar.graphviz.dsl.mutableNode
import guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT
import guru.nidi.graphviz.attribute.Rank.dir
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
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
                val currentComponent = component.currentComponent()
                val componentName = ClassName.get(currentComponent)

                val outputFile = filer.createResource(
                    StandardLocation.CLASS_OUTPUT,
                    componentName.toString(),
                    Joiner.on('_').join(componentName.simpleNames()) + ".png"
                )

                val builder = mutGraph(
                    name = currentComponent.toString(),
                    directed = true
                ) {
                    attr {
                        add(dir(LEFT_TO_RIGHT))
                        add("labeljust", "l")
                        add("label" to name())
                        add("compound" to true)
                    }
                    nodes.forEach {
                        mutableNode(it.componentPath().currentComponent().simpleName.toString())
                    }
                }

                Graphviz.fromGraph(builder)
                    .render(Format.PNG)
                    .toOutputStream(outputFile.openOutputStream())
            }
    }
}