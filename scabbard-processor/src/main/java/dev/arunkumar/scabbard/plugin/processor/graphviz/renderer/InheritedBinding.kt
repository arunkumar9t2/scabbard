package dev.arunkumar.scabbard.plugin.processor.graphviz.renderer

import dagger.model.Binding
import dagger.model.BindingGraph
import dagger.model.Scope
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.scabbard.plugin.parser.NewLine
import dev.arunkumar.scabbard.plugin.parser.name
import dev.arunkumar.scabbard.plugin.processor.graphviz.RenderingContext

data class InheritedBinding(
  private val componentNode: BindingGraph.ComponentNode,
  private val bindings: List<Binding>
) {
  class GraphRenderer(
    override val renderingContext: RenderingContext
  ) : Renderer<List<InheritedBinding>> {
    override fun DotGraphBuilder.build(renderingElement: List<InheritedBinding>) {
      renderingElement.forEach { inheritedBinding ->
        val componentNode = inheritedBinding.componentNode
        val typeNameExtractor = renderingContext.typeNameExtractor

        val currentComponentPath = componentNode.componentPath()
        val componentType = currentComponentPath.currentComponent().asType()

        val componentName = buildString {
          append(typeNameExtractor.extractName(componentType))
          if (componentNode.scopes().isNotEmpty()) {
            append(NewLine)
            append(componentNode.scopes().map(Scope::name).last())
          }
        }
        cluster(typeNameExtractor.extractName(currentComponentPath)) {
          graphAttributes {
            "labeljust" eq "c"
            "label" eq "Inherited from $componentName"
            "style" eq "dashed"
            "href" eq renderingContext.href(componentNode)
            componentNode.scopes().map(Scope::name).forEach { scope ->
              "color" eq renderingContext.scopeColor(scope)
            }
          }

          BindingsRenderer(renderingContext).render(this, inheritedBinding.bindings)
        }
      }
    }
  }
}
