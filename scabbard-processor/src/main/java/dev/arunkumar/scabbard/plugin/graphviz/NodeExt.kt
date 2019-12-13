package dev.arunkumar.scabbard.plugin.graphviz

import dagger.model.Binding
import dagger.model.BindingGraph
import dagger.model.BindingKind

fun BindingGraph.Node.label(): String = when (this) {
  is Binding -> {
    var name = key().toString()
    val scopeName = scopeName()
    val isSubComponentCreator = kind() == BindingKind.SUBCOMPONENT_CREATOR
    val isMultibinding = kind().isMultibinding
    val newLine = "\\n"

    val isDelegate = kind() == BindingKind.DELEGATE
    if (isDelegate) {
      name = key().multibindingContributionIdentifier().get()
        .let { it.module().split(".").last() + "." + it.bindingElement() }
    }

    buildString {
      scopeName?.let {
        append(scopeName)
        append(newLine)
      }
      append(name)
      if (isSubComponentCreator) {
        append(newLine)
        append("Subcomponent Creator")
      }
      if (isMultibinding) {
        append(newLine)
        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (kind()) {
          BindingKind.MULTIBOUND_MAP -> append("MAP")
          BindingKind.MULTIBOUND_SET -> append("SET")
        }
      }
    }
  }
  else -> componentPath().toString()
}