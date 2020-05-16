package dev.arunkumar.scabbard.plugin.processor.graphviz

import dagger.Binds
import dagger.Module
import dagger.model.Binding
import dagger.model.BindingGraph
import dagger.model.BindingGraph.MaybeBinding
import dev.arunkumar.dot.DotGraph
import dev.arunkumar.scabbard.plugin.BindingGraphProcessor
import dev.arunkumar.scabbard.plugin.di.ProcessorScope
import dev.arunkumar.scabbard.plugin.options.ScabbardOptions
import dev.arunkumar.scabbard.plugin.output.OutputWriter
import dev.arunkumar.scabbard.plugin.parser.subcomponentsOf
import dev.arunkumar.scabbard.plugin.processor.graphviz.renderer.DaggerComponent
import dev.arunkumar.scabbard.plugin.util.processingBlock
import javax.inject.Inject
import javax.lang.model.element.TypeElement
import kotlin.collections.component1
import kotlin.collections.component2

@Suppress("UnstableApiUsage")
@ProcessorScope
@JvmSuppressWildcards
class GraphVizBindingGraphProcessor
@Inject
constructor(
  override val bindingGraph: BindingGraph,
  private val scabbardOptions: ScabbardOptions,
  private val outputWriters: Set<OutputWriter>,
  private val renderingContext: RenderingContext
) : BindingGraphProcessor {

  override fun process() = processingBlock(scabbardOptions) {
    val network = bindingGraph.network()

    // Group all the nodes by their component
    network.nodes().asSequence()
      .groupBy { it.componentPath() }
      .map { (componentPath, nodes) ->
        val currentComponent = componentPath.currentComponent()

        val subcomponents = bindingGraph.subcomponentsOf(currentComponent)
        val entryPoints = nodes.filterIsInstance<Binding>().filter(renderingContext::isEntryPoint)
        val dependencyNodes = nodes.filterIsInstance<MaybeBinding>().filterNot(renderingContext::isEntryPoint)
        val edges = nodes.flatMap(network::incidentEdges).distinct()

        val dotGraphBuilder = renderingContext.createRootDotGraphBuilder(componentPath)

        // Drop the id cache to render only nodes present in this component/subcomponent
        renderingContext.dropIdCache()

        // Render this component's graph
        DaggerComponent.GraphRenderer(renderingContext).render(
          dotGraphBuilder,
          DaggerComponent(componentPath, entryPoints, dependencyNodes, subcomponents, edges)
        )
        return@map currentComponent to dotGraphBuilder.dotGraph
      }.forEach { (currentComponent, dotGraph) -> writeOutput(currentComponent, dotGraph) }
  }

  private fun writeOutput(currentComponent: TypeElement, dotGraph: DotGraph) {
    val dotString = dotGraph.toString()
    outputWriters.forEach { writer ->
      writer.write(dotString, currentComponent, bindingGraph.isFullBindingGraph)
    }
  }

  @Module
  interface Builder {
    @Binds
    fun bindingGraphProcessor(
      graphVizBindingGraphProcessor: GraphVizBindingGraphProcessor
    ): BindingGraphProcessor
  }
}