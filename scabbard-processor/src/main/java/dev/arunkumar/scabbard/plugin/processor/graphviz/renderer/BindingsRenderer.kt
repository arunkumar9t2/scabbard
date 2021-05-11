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
      "label" `=` missingBinding.key().toString() // TODO(arun) Update label calculation for MissingBinding
      "color" `=` "firebrick1"
    }
  }

  private fun DotGraphBuilder.binding(binding: Binding) {
    binding.id {
      "label" `=` binding.label
      "color" `=` binding.color
      when {
        binding.kind().isMultibinding -> {
          "shape" `=` "tab"
        }
        binding.kind() == BOUND_INSTANCE -> {
          "shape" `=` "parallelogram"
        }
        binding.isEntryPoint -> {
          "shape" `=` "component"
          if (binding.kind() == MEMBERS_INJECTION) {
            "label" `=` "inject (${binding.label})"
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
            "label" `=` name
            "labeljust" `=` "c"
            "style" `=` "rounded"
            "color" `=` "black"
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

  override fun DotGraphBuilder.build(renderElement: List<MaybeBinding>) {
    renderElement.forEach { maybeBinding ->
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
    addMultiBindings(renderElement.asSequence())
  }
}
