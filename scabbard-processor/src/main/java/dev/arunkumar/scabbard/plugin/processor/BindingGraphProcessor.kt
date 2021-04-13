package dev.arunkumar.scabbard.plugin.processor

import dagger.model.BindingGraph

interface BindingGraphProcessor {
  val bindingGraph: BindingGraph
  fun process()
}
