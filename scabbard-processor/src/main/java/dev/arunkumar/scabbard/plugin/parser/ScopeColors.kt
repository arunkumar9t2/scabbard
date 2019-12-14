package dev.arunkumar.scabbard.plugin.parser

import com.google.common.collect.Iterators
import dev.arunkumar.scabbard.plugin.di.ProcessorScope
import javax.inject.Inject

@ProcessorScope
class ScopeColors @Inject constructor() {

  //TODO(arun) Add more colors?
  private val scopeColors by lazy {
    listOf(
      "aquamarine",
      "bisque",
      "brown1",
      "chartreuse1",
      "coral",
      "cornflowerblue",
      "darkgoldenrod1",
      "darkolivegreen1",
      "darkorchid1",
      "deeppink",
      "deepskyblue",
      "skyblue1",
      "salmon1",
      "firebrick1",
      "green1",
      "seagreen1",
      "yellow1",
      "yellowgreen"
    )
  }
  private val scopeColorsCycler by lazy { Iterators.cycle(scopeColors) }

  private val scopeColorsCache = mutableMapOf("" to "turquoise")

  operator fun get(scopeName: String): String {
    return scopeColorsCache.getOrPut(scopeName) { scopeColorsCycler.next() }
  }
}