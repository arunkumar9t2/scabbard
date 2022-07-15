/*
 * Copyright 2022 Arunkumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.arunkumar.scabbard.plugin.store

import com.google.common.collect.Iterators
import dev.arunkumar.scabbard.plugin.di.VisitGraphScope
import javax.inject.Inject

/**
 * Class to cache and assign colors to Dagger scopes. The colors are
 * assigned in cyclic order.
 */
@VisitGraphScope
class DaggerScopeColors @Inject constructor() {

  // TODO(arun) Add more colors?
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
