package dev.arunkumar.scabbard.plugin

import dagger.model.BindingGraph

interface BindingGraphProcessor {
    val bindingGraph: BindingGraph
    fun process()
}