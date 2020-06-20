package dev.arunkumar.scabbard.plugin.parser

import com.google.common.collect.Iterators
import dev.arunkumar.scabbard.plugin.di.ProcessorScope
import javax.inject.Inject

/**
 * Class to cache and assign colors to Dagger scopes. The colors are assigned in cyclic order.
 */
@ProcessorScope
class DaggerScopeColors @Inject constructor() {

  //TODO(arun) Add more colors?
  private val scopeColors by lazy {
    listOf(
      "aquamarine",
      "bisque",
      "yellow1",
      "chartreuse2",
      "coral",
      "lightblue1",
      "darkgoldenrod1",
      "darkolivegreen1",
      "darkorchid1",
      "deeppink",
      "deepskyblue",
      "skyblue1",
      "salmon1",
      "green1",
      "seagreen1",
      "thistle",
      "yellowgreen"
    )
  }
  private val scopeColorsCycler by lazy { Iterators.cycle(scopeColors) }

  private val scopeColorsCache = mutableMapOf("" to "turquoise")

  operator fun get(scopeName: String): String {
    return scopeColorsCache.getOrPut(scopeName) { scopeColorsCycler.next() }
  }
}