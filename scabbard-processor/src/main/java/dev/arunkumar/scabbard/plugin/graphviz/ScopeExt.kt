package dev.arunkumar.scabbard.plugin.graphviz

import dagger.model.Scope

inline val Scope.name get() = "@${scopeAnnotationElement().simpleName}"