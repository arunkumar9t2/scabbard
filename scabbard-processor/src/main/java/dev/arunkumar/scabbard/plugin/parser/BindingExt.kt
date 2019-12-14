package dev.arunkumar.scabbard.plugin.parser

import dagger.model.Binding

internal fun Binding.scopeName() = when {
  scope().isPresent -> scope().get().name
  else -> null
}