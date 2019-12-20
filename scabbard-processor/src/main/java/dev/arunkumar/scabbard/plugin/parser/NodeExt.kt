package dev.arunkumar.scabbard.plugin.parser

import dagger.model.Binding
import dagger.model.BindingGraph.ComponentNode
import dagger.model.BindingGraph.Node
import dagger.model.BindingKind.*
import dev.arunkumar.scabbard.plugin.options.ScabbardOptions

private const val newLine = "\\n"

private data class MultiBindingData(val isMultiBinding: Boolean, val type: String)

internal fun Node.calculateLabel(scabbardOptions: ScabbardOptions): String = when (this) {
  is Binding -> {
    try {
      var name = key().toString().replace(" ", newLine)
      val scopeName = scopeName()
      val isSubComponentCreator = kind() == SUBCOMPONENT_CREATOR

      val multiBindingData = MultiBindingData(
        isMultiBinding = kind().isMultibinding,
        type = when (kind()) {
          MULTIBOUND_MAP -> "MAP"
          MULTIBOUND_SET -> "SET"
          else -> "UNKNOWN"
        }
      )

      key().multibindingContributionIdentifier().ifPresent { identifier ->
        name = identifier.let { it.module().split(".").last() + "." + it.bindingElement() }
      }

      buildLabel(
        name,
        scopeName,
        isSubComponentCreator,
        multiBindingData
      )
    } catch (e: Exception) {
      e.printStackTrace()
      "Errored $this"
    }
  }
  is ComponentNode -> {
    val name = componentPath().currentComponent().qualifiedName.toString()
    val scopeName = scopes().takeIf { it.isNotEmpty() }?.joinToString(separator = "|") { it.name }
    buildLabel(name, scopeName)
  }
  else -> componentPath().toString()
}

private fun buildLabel(
  name: String,
  scopeName: String? = null,
  isSubComponentCreator: Boolean = false,
  multiBindingData: MultiBindingData? = null
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
  if (multiBindingData?.isMultiBinding == true) {
    append(newLine)
    append(multiBindingData.type)
  }
}