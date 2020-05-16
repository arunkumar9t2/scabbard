package dev.arunkumar.scabbard.plugin.parser

import dagger.model.Binding
import dagger.model.BindingGraph
import dagger.model.Scope
import javax.lang.model.element.TypeElement

// TODO(arun) Memoize component nodes
internal fun BindingGraph.subcomponentsOf(parent: TypeElement): List<BindingGraph.ComponentNode> =
  componentNodes()
    .filter { node ->
      val componentPath = node.componentPath()
      node.isSubcomponent
          && !componentPath.atRoot()
          && componentPath.parent().currentComponent() == parent
    }

internal fun Binding.scopeName() = when {
  scope().isPresent -> scope().get().name
  else -> null
}

private const val newLine = "\\n"

fun buildLabel(
  name: String,
  qualifier: String? = null,
  scopeName: String? = null,
  isSubComponentCreator: Boolean = false
) = buildString {
  qualifier?.let {
    append(qualifier)
    append(newLine)
  }
  scopeName?.let {
    append(scopeName)
    append(newLine)
  }
  append(name)
  if (isSubComponentCreator) {
    append(newLine)
    append(newLine)
    append("Subcomponent Creator")
  }
}


internal inline val Scope.name get() = "@${scopeAnnotationElement().simpleName}"