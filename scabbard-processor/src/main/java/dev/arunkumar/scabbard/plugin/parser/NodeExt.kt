package dev.arunkumar.scabbard.plugin.parser

import dagger.model.Binding
import dagger.model.BindingGraph.ComponentNode
import dagger.model.BindingGraph.Node
import dagger.model.BindingKind.SUBCOMPONENT_CREATOR

private const val newLine = "\\n"

/**
 * Calculates a node's name.
 *
 * @param typeNameExtractor - Uses the instance to calculate the name of the type. Calls [TypeNameExtractor.extractName] to
 * get the name,
 */
internal fun Node.calculateLabel(typeNameExtractor: TypeNameExtractor): String = when (this) {
  is Binding -> {
    try {
      // Process qualifiers separately
      val qualifier = ""
      var name = typeNameExtractor.extractName(key().type())

      val scopeName = scopeName()
      val isSubComponentCreator = kind() == SUBCOMPONENT_CREATOR

      key().multibindingContributionIdentifier().ifPresent { identifier ->
        name = identifier.let { it.module().split(".").last() + "." + it.bindingElement() }
      }

      buildLabel(name, scopeName, isSubComponentCreator)
    } catch (e: Exception) {
      e.printStackTrace()
      "Errored $this"
    }
  }
  is ComponentNode -> {
    val name = typeNameExtractor.extractName(componentPath().currentComponent())
    val scopeName = scopes().takeIf { it.isNotEmpty() }?.joinToString(separator = "|") { it.name }
    buildLabel(name, scopeName)
  }
  else -> toString()
}

private fun buildLabel(
  name: String,
  scopeName: String? = null,
  isSubComponentCreator: Boolean = false
) = buildString {
  scopeName?.let {
    append(scopeName)
    append(newLine)
  }
  append(name)
  if (isSubComponentCreator) {
    append(newLine)
    append("Subcomponent Creator")
  }
}