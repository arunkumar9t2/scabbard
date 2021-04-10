package dev.arunkumar.scabbard.plugin.processor.graphviz

import com.google.common.graph.Graph
import com.google.common.graph.GraphBuilder
import com.google.common.graph.ImmutableGraph
import com.google.common.graph.MutableGraph
import dagger.model.BindingGraph
import dev.arunkumar.dot.DotGraph
import dev.arunkumar.dot.dsl.directedGraph
import dev.arunkumar.scabbard.plugin.di.VisitGraphScope
import dev.arunkumar.scabbard.plugin.options.ScabbardOptions
import dev.arunkumar.scabbard.plugin.output.OutputWriter
import dev.arunkumar.scabbard.plugin.parser.TypeNameExtractor
import dev.arunkumar.scabbard.plugin.processor.BindingGraphProcessor
import dev.arunkumar.scabbard.plugin.processor.graphviz.renderer.ComponentTreeRenderer
import dev.arunkumar.scabbard.plugin.util.processingBlock
import javax.inject.Inject
import javax.lang.model.element.TypeElement

/**
 * A [BindingGraphProcessor] that generates image of component hierarchy.
 */
@Suppress("UnstableApiUsage")
@VisitGraphScope
class ComponentTreeProcessor
@Inject
constructor(
  override val bindingGraph: BindingGraph,
  private val scabbardOptions: ScabbardOptions,
  private val outputWriters: Set<@JvmSuppressWildcards OutputWriter>,
  private val renderingContext: RenderingContext,
  private val typeNameExtractor: TypeNameExtractor
) : BindingGraphProcessor {

  @ExperimentalStdlibApi
  override fun process() = processingBlock(scabbardOptions) {
    if (bindingGraph.isFullBindingGraph) return@processingBlock

    val rootComponentNode = bindingGraph.rootComponentNode()
    val rootComponentType = rootComponentNode.componentPath().currentComponent()

    val componentTree = constructComponentTree()

    val dotGraphBuilder = directedGraph(rootComponentNode.componentPath().toString()) {
      graphAttributes {
        "rankdir" eq "TB"
        "label" eq typeNameExtractor.extractName(rootComponentNode.componentPath())
        "compound" eq true
        "labeljust" eq "l"
        "pad" eq 0.2
      }
      nodeAttributes {
        "shape" eq "rectangle"
        "style" eq "filled"
        "color" eq "turquoise"
      }
    }

    ComponentTreeRenderer(renderingContext).render(dotGraphBuilder, componentTree)

    writeOutput(rootComponentType, dotGraph = dotGraphBuilder.dotGraph)
  }

  private fun writeOutput(currentComponent: TypeElement, dotGraph: DotGraph) {
    outputWriters.forEach { writer ->
      writer.write(
        dotString = dotGraph.toString(),
        component = currentComponent,
        isFull = bindingGraph.isFullBindingGraph,
        isComponentTree = true
      )
    }
  }

  /**
   * Constructs a component tree from `BindingGraph`. For example consider graph like this:
   *                   +------+
   *                   | Root |
   *                   +--+-+-+
   *                      | |
   *            v---------+ +------v
   *    +---------------+   +---------------+
   *    |Subcomponent A |   |Subcomponent B |
   *    +---------------+   +------+--------+
   *                               v
   *                        +------+--------+
   *                        |Subcomponent C |
   *                        +---------------+
   * This information of component relationships is already present via `componentPath`s in each `ComponentNode` i.e,
   * `componentPath.components` will give ordered list representing the hierarchy. For example, for the above component,
   *  * Root
   *  * Root -> Subcomponent A
   *  * Root -> Subcomponent A -> Subcomponent C
   *
   *  From such ordered lists, this function constructs a [Graph] instance and returns it.
   */
  @ExperimentalStdlibApi
  private fun constructComponentTree(): Graph<BindingGraph.ComponentNode> {
    val componentNodes = bindingGraph.componentNodes()
    val rootComponentNode = bindingGraph.rootComponentNode()

    // Create a custom tree of ComponentNodes.
    val componentTree: MutableGraph<BindingGraph.ComponentNode> = GraphBuilder
      .directed()
      .allowsSelfLoops(false)
      .expectedNodeCount(componentNodes.size)
      .build()

    val componentNodeTypeCache = componentNodes
      .map { it.componentPath().currentComponent() to it }
      .toMap()

    componentNodes
      .map { it.componentPath().components() }
      .forEach { componentHierarchy ->
        componentHierarchy
          .map { componentNodeTypeCache.getValue(it) }
          .scan(rootComponentNode) { current, next ->
            if (current != next) {
              componentTree.addNode(current)
              componentTree.addNode(next)
              componentTree.putEdge(current, next)
            }
            next
          }
      }
    return ImmutableGraph.copyOf(componentTree)
  }
}
