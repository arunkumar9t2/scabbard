package dev.arunkumar.scabbard.plugin.processor.graphviz.renderer

import dagger.model.Binding
import dagger.model.BindingGraph.MaybeBinding
import dagger.model.BindingGraph.MissingBinding
import dagger.model.BindingKind.BOUND_INSTANCE
import dagger.model.BindingKind.MEMBERS_INJECTION
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.scabbard.plugin.processor.graphviz.RenderingContext

/**
 * Renders the actual dependency graph nodes in the current context accounting for missing nodes, multibindings and
 * entry points.
 */
class BindingsRenderer(
  override val renderingContext: RenderingContext
) : Renderer<List<MaybeBinding>> {

  private fun DotGraphBuilder.missingBinding(missingBinding: MissingBinding) {
    missingBinding.id {
      "label" eq missingBinding.key().toString() // TODO(arun) Update label calculation for MissingBinding
      "color" eq "firebrick1"
    }
  }

  private fun DotGraphBuilder.binding(binding: Binding) {
    binding.id {
      "label" eq binding.label
      "color" eq binding.color
      when {
        binding.kind().isMultibinding -> {
          "shape" eq "tab"
        }
        binding.kind() == BOUND_INSTANCE -> {
          "shape" eq "parallelogram"
        }
        binding.isEntryPoint -> {
          "shape" eq "component"
          if (binding.kind() == MEMBERS_INJECTION) {
            "label" eq "inject (${binding.label})"
          }
        }
      }
    }
  }

  private fun DotGraphBuilder.addMultiBindings(currentComponentNodes: Sequence<MaybeBinding>) {
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
            "color" eq "black"
          }

          binding(multiBinding)

          if (!multiBinding.isEntryPoint) {
            // If multbinding node was present as an entry point does not make sense to render its' content in
            // entry point cluster.
            renderingContext.bindingGraph
              .requestedBindings(multiBinding)
              .forEach { binding -> binding(binding) }
          }
        }
      }
  }

  override fun DotGraphBuilder.build(renderingElement: List<MaybeBinding>) {
    renderingElement.forEach { maybeBinding ->
      when (maybeBinding) {
        is Binding -> {
          if (!maybeBinding.kind().isMultibinding) {
            // Multibindings are handled in another cluster
            binding(maybeBinding)
          }
        }
        is MissingBinding -> missingBinding(maybeBinding)
      }
    }
    // Add multi bindings
    addMultiBindings(renderingElement.asSequence())
  }
}
