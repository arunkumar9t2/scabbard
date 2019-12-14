package dev.arunkumar.scabbard.plugin.parser

import dagger.model.Binding
import dagger.model.BindingGraph.ComponentNode
import dagger.model.BindingGraph.Node
import dagger.model.BindingKind
import java.io.PrintWriter
import java.io.StringWriter

private const val newLine = "\\n"

private data class MultiBindingData(val isMultiBinding: Boolean, val type: String)

internal fun Node.label(): String = when (this) {
  is Binding -> {
    try {
      var name = key().toString()
      val scopeName = scopeName()
      val isSubComponentCreator = kind() == BindingKind.SUBCOMPONENT_CREATOR

      val multiBindingData = MultiBindingData(
        isMultiBinding = kind().isMultibinding,
        type = when (kind()) {
          BindingKind.MULTIBOUND_MAP -> "MAP"
          BindingKind.MULTIBOUND_SET -> "SET"
          else -> "UNKNOWN"
        }
      )

      if (kind() == BindingKind.DELEGATE) {
        name = key().multibindingContributionIdentifier().get()
          .let { it.module().split(".").last() + "." + it.bindingElement() }
      }

      buildLabel(
        name,
        scopeName,
        isSubComponentCreator,
        multiBindingData
      )
    } catch (e: Exception) {
      e.printStackTrace()
      "Errored ${StringWriter().also { e.printStackTrace(PrintWriter(it)) }}"
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