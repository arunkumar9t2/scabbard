package dev.arunkumar.scabbard.plugin.processor.graphviz.renderer

import dagger.model.Binding
import dagger.model.BindingGraph.MaybeBinding
import dagger.model.BindingGraph.MissingBinding
import dagger.model.BindingKind
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.scabbard.plugin.processor.graphviz.RenderingContext

/**
 * Renders the actual dependency graph nodes in the current context accounting for missing nodes, multibindings,
 * and component nodes.
 */
class DependenciesRenderer(
  override val renderingContext: RenderingContext
) : Renderer<List<MaybeBinding>> {

  private fun DotGraphBuilder.missingBinding(missingBinding: MissingBinding) {
    missingBinding.id {
      "label" eq missingBinding.key().toString() // TODO(arun) Update label calculation for MissingBinding
      "color" eq "firebrick1"
    }
  }

  private fun DotGraphBuilder.dependencyBinding(binding: Binding) {
    if (binding.kind().isMultibinding) return // Multi binding rendered as another cluster
    binding.id {
      "label" eq binding.label
      "color" eq binding.color
      if (binding.kind() == BindingKind.BOUND_INSTANCE) {
        "shape" eq "parallelogram"
      }
    }
  }

  private fun DotGraphBuilder.addMultiBindings(currentComponentNodes: Sequence<MaybeBinding>) {
    // TODO(arun) Create a custom renderer for this and avoid abusing rendering context
    currentComponentNodes
      .filterIsInstance<Binding>()
      .filter { it.kind().isMultibinding }
      .forEach { multiBinding ->
        val name = renderingContext.typeNameExtractor.extractName(multiBinding.key().type())
        cluster(name) {
          graphAttributes {
            "label" eq name
            "labeljust" eq "c"
            "style" eq "rounded"
          }
          multiBinding.id {
            "shape" eq "tab"
            "label" eq multiBinding.label
            "color" eq multiBinding.color
          }
          renderingContext.bindingGraph
            .requestedBindings(multiBinding)
            .forEach { binding -> dependencyBinding(binding) }
        }
      }
  }

  override fun DotGraphBuilder.build(renderingElement: List<MaybeBinding>) {
    cluster("Dependency Graph") {
      graphAttributes {
        "labeljust" eq "l"
        "label" eq "Dependency Graph"
      }
      // Add dependency graph
      renderingElement.forEach { node ->
        when (node) {
          is Binding -> dependencyBinding(node)
          is MissingBinding -> missingBinding(node)
        }
      }
      // Add multi bindings
      addMultiBindings(renderingElement.asSequence())
    }
  }
}