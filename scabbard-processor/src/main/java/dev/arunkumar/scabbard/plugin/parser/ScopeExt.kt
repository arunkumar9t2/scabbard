package dev.arunkumar.scabbard.plugin.parser

import dagger.model.Scope

internal inline val Scope.name get() = "@${scopeAnnotationElement().simpleName}"