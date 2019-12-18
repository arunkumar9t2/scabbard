package dev.arunkumar.scabbard.plugin.processor.graphviz

import dagger.model.Binding
import dagger.model.BindingGraph
import dagger.model.BindingGraph.*
import dagger.model.BindingKind.DELEGATE
import dagger.model.BindingKind.MEMBERS_INJECTION
import dagger.model.ComponentPath
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.dot.dsl.directedGraphBuilder
import dev.arunkumar.scabbard.plugin.BindingGraphProcessor
import dev.arunkumar.scabbard.plugin.di.ProcessorScope
import dev.arunkumar.scabbard.plugin.options.ScabbardOptions
import dev.arunkumar.scabbard.plugin.parser.*
import dev.arunkumar.scabbard.plugin.util.component1
import dev.arunkumar.scabbard.plugin.util.component2
import dev.arunkumar.scabbard.plugin.util.exceptionHandler
import dev.arunkumar.scabbard.plugin.util.processingBlock
import dev.arunkumar.scabbard.plugin.writer.OutputWriter
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import java.util.*
import javax.inject.Inject

@ProcessorScope
class GraphVizBindingGraphProcessor
@Inject
constructor(
  override val bindingGraph: BindingGraph,
  private val scabbardOptions: ScabbardOptions,
  private val scopeColors: ScopeColors,
  private val outputWriter: OutputWriter
) : BindingGraphProcessor {

  private val Binding.color get() = scopeColors[scopeName() ?: ""]
  private val Binding.isEntryPoint get() = bindingGraph.entryPointBindings().contains(this)

  private val globalNodeIds = mutableMapOf<Node, String>()
  private val Node.id get() = globalNodeIds.getOrPut(this) { UUID.randomUUID().toString() }

  override fun process() = processingBlock(scabbardOptions) {
    val network = bindingGraph.network()
    val nodes = network.nodes()
    val allEdges = network.edges()
    nodes.asSequence()
      .groupBy { it.componentPath() }
      .forEach { (component, componentNodes) ->
        globalNodeIds.clear()

        val currentComponent = component.currentComponent()
        val subcomponents = bindingGraph.subcomponents(currentComponent)

        val dotGraphBuilder = buildGraph(
          currentComponent = component,
          subcomponents = subcomponents,
          nodes = componentNodes.asSequence(),
          edges = allEdges.asSequence() // TODO(arun) why pass global edges here?
        )

        scabbardOptions.exceptionHandler {
          val (outputFile, dotFile) = outputWriter.createOutputFiles(currentComponent)
          val dotOutput = dotGraphBuilder.dotGraph.toString()

          Graphviz.fromString(dotOutput)
            .scale(1.2)
            .render(Format.PNG)
            .toOutputStream(outputFile.openOutputStream())
          dotFile.openOutputStream().write(dotOutput.toByteArray())
        }
      }
  }

  private fun buildGraph(
    currentComponent: ComponentPath,
    subcomponents: Sequence<ComponentNode>,
    nodes: Sequence<Node>,
    edges: Sequence<Edge>
  ): DotGraphBuilder = directedGraphBuilder(currentComponent.toString()) {
    graphAttributes {
      "rankdir" eq "LR"
      "labeljust" eq "l"
      "label" eq currentComponent.toString()
      "pad" eq 0.2
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
          is Binding -> addDependencyBinding(node)
          is MissingBinding -> addMissingBinding(node)
          is ComponentNode -> addComponentNode(node)
        }
      }

      // Add multi bindings
      addMultiBindings(nodes)
    }

    cluster("Subcomponents") {
      graphAttributes {
        "labeljust" eq "l"
        "shape" eq "folder"
        "label" eq "Subcomponents"
      }
      subcomponents.forEach { subcomponent -> addSubcomponent(subcomponent) }
    }

    // Render edges between all nodes
    edges.forEach { edge ->
      val (source, target) = bindingGraph.network().incidentNodes(edge)
      if (globalNodeIds.containsKey(source) && globalNodeIds.containsKey(target)) {
        addEdge(edge, source, target)
      }
    }
  }

  private fun DotGraphBuilder.addEntryPoints(nodes: Sequence<Node>) = nodes
    .filterIsInstance<Binding>()
    .filter { it.isEntryPoint }
    .forEach { node ->
      val label = when (node.kind()) {
        MEMBERS_INJECTION -> "inject ( ${node.label()} )"
        else -> node.label()
      }
      node.id {
        "shape" eq "component"
        "label" eq label
        "penwidth" eq 2
      }
    }

  private fun DotGraphBuilder.addDependencyBinding(binding: Binding) {
    if (binding.isEntryPoint) return // Entry points are rendered in another cluster
    if (binding.kind().isMultibinding) return // Multi binding rendered as another cluster
    binding.id {
      "label" eq binding.label()
      "color" eq binding.color
    }
  }

  private fun DotGraphBuilder.addMissingBinding(missingBinding: MissingBinding) {
    missingBinding.id {
      "label" eq missingBinding.key().toString() // TODO(arun) Update label calculation for MissingBinding
      "color" eq "firebrick1"
    }
  }

  private fun DotGraphBuilder.addComponentNode(node: ComponentNode) {
    node.id {
      "style" eq "invis"
      "shape" eq "point"
    }
  }

  private fun DotGraphBuilder.addSubcomponent(subcomponent: ComponentNode) {
    subcomponent.id {
      "label" eq subcomponent.label()
      // TODO(arun) will multiple scopes be present? If yes, can it be visualized?
      subcomponent.scopes().forEach { scope ->
        "color" eq scopeColors[scope.name]
      }
    }
  }

  private fun DotGraphBuilder.addMultiBindings(currentComponentNodes: Sequence<Node>) {
    currentComponentNodes
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
            .forEach { binding -> addDependencyBinding(binding) }
        }
      }
  }

  private fun DotGraphBuilder.addEdge(edge: Edge, source: Node, target: Node) {
    when (edge) {
      is DependencyEdge -> {
        if (!edge.isEntryPoint) {
          (source.id link target.id) {
            if ((source as? Binding)?.kind() == DELEGATE) {
              // Delegate edges i.e usually using @Binds
              "style" eq "dotted"
              "label" eq "delegates"
            }

            // Handle missing binding
            if (source is MissingBinding || target is MissingBinding) {
              "style" eq "dashed"
              "arrowType" eq "empty"
              val labelLocation = if (source is MissingBinding) "taillabel" else "headlabel"
              labelLocation eq "Missing binding"
            }
          }
        }
      }
      is ChildFactoryMethodEdge -> {
        (source.id link target.id) {
          "style" eq "dashed"
          "taillabel" eq edge.factoryMethod()
        }
      }
      is SubcomponentCreatorBindingEdge -> {
        (source.id link target.id) {
          "style" eq "dashed"
          "xlabel" eq "subcomponent"
        }
      }
    }
  }
}