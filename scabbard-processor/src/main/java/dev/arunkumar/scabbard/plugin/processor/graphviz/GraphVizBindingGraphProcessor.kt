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
import dev.arunkumar.scabbard.plugin.util.tryCatchLogging
import dev.arunkumar.scabbard.plugin.writer.OutputWriter
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import java.util.*
import javax.annotation.processing.Filer
import javax.inject.Inject

@ProcessorScope
class GraphVizBindingGraphProcessor
@Inject
constructor(
  override val bindingGraph: BindingGraph,
  private val scabbardOptions: ScabbardOptions,
  private val filer: Filer,
  private val scopeColors: ScopeColors,
  private val outputWriter: OutputWriter
) : BindingGraphProcessor {

  private val Binding.color get() = scopeColors[scopeName() ?: ""]
  private val Binding.isEntryPoint get() = bindingGraph.entryPointBindings().contains(this)

  private val globalNodeIds = mutableMapOf<Node, String>()
  private val Node.id get() = globalNodeIds.getOrPut(this) { UUID.randomUUID().toString() }

  override fun process() = tryCatchLogging {
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
          component,
          subcomponents,
          componentNodes.asSequence(),
          allEdges.asSequence() // TODO(arun) why pass global edges here?
        )
        val (outputFile, dotFile) = outputWriter.createOutputFiles(currentComponent)
        val dotOutput = dotGraphBuilder.dotGraph.toString()

        Graphviz.fromString(dotOutput)
          .scale(1.2)
          .render(Format.PNG)
          .toOutputStream(outputFile.openOutputStream())
        dotFile.openOutputStream().write(dotOutput.toByteArray())
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
          is Binding -> addDependencyNode(node)
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

  private fun DotGraphBuilder.addDependencyNode(node: Binding) {
    if (node.isEntryPoint) return // Entry points are rendered in another cluster
    if (node.kind().isMultibinding) return // Multi binding rendered as another cluster
    node.id {
      "label" eq node.label()
      "color" eq node.color
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
            .forEach { binding -> addDependencyNode(binding) }
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