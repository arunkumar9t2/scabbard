package dev.arunkumar.scabbard.plugin.processor.graphviz

import dagger.model.Binding
import dagger.model.BindingGraph
import dagger.model.BindingGraph.Node
import dagger.model.BindingKind
import dagger.model.ComponentPath
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.dot.dsl.directedGraph
import dev.arunkumar.scabbard.plugin.options.ScabbardOptions
import dev.arunkumar.scabbard.plugin.output.OutputManager
import dev.arunkumar.scabbard.plugin.parser.*
import java.util.*
import javax.inject.Inject

class RenderingContext
@Inject
constructor(
  val bindingGraph: BindingGraph,
  val typeNameExtractor: TypeNameExtractor,
  private val scabbardOptions: ScabbardOptions,
  private val daggerScopeColors: DaggerScopeColors,
  private val outputManager: OutputManager
) {

  fun isEntryPoint(binding: Binding) = bindingGraph.entryPointBindings().contains(binding)

  fun color(binding: Binding) = scopeColor(binding.scopeName() ?: "")

  private val nodeIdCache = mutableMapOf<Node, String>()

  fun nodeId(node: Node) = nodeIdCache.getOrPut(node) { UUID.randomUUID().toString() }

  fun scopeColor(scopeName: String): String = daggerScopeColors[scopeName]

  fun nodeLabel(node: Node) = node.run {
    when (this) {
      is Binding -> {
        try {
          val qualifier = key().qualifier().orElse(null)?.let { annotationMirror ->
            typeNameExtractor.extractName(annotationMirror)
          }

          var name = typeNameExtractor.extractName(key().type())

          val scopeName = scopeName()
          val isSubComponentCreator = kind() == BindingKind.SUBCOMPONENT_CREATOR

          key().multibindingContributionIdentifier().ifPresent { identifier ->
            name = typeNameExtractor.extractName(identifier)
          }

          buildLabel(
            name,
            qualifier,
            scopeName,
            isSubComponentCreator
          )
        } catch (e: Exception) {
          e.printStackTrace()
          "Errored $this"
        }
      }
      is BindingGraph.ComponentNode -> {
        val name = typeNameExtractor.extractName(componentPath().currentComponent().asType())
        val scopeName = scopes().takeIf { it.isNotEmpty() }?.joinToString(separator = "|") { it.name }
        buildLabel(name = name, scopeName = scopeName)
      }
      else -> toString()
    }
  }

  fun href(componentNode: BindingGraph.ComponentNode): String {
    return outputManager.outputFileNameFor(
      scabbardOptions.outputImageFormat,
      componentNode.componentPath().currentComponent(),
      bindingGraph.isFullBindingGraph
    )
  }

  fun createRootDotGraphBuilder(currentComponentPath: ComponentPath): DotGraphBuilder {
    return directedGraph(currentComponentPath.toString()) {
      graphAttributes {
        "rankdir" eq "LR"
        "labeljust" eq "l"
        "label" eq typeNameExtractor.extractName(currentComponentPath)
        "pad" eq 0.2
        "compound" eq true
      }
      nodeAttributes {
        "shape" eq "rectangle"
        "style" eq "filled"
        "color" eq "turquoise"
      }
    }
  }

  fun validInContext(source: Node, target: Node): Boolean {
    return nodeIdCache.containsKey(source) && nodeIdCache.containsKey(target)
  }
}