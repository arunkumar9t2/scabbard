package dev.arunkumar.scabbard.plugin.graphviz

import dagger.model.Binding

internal fun Binding.scopeName() = when {
    scope().isPresent -> "@" + scope().get().scopeAnnotationElement().simpleName.toString()
    else -> null
}