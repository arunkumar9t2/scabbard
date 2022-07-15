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

package dev.arunkumar.scabbard.plugin.processor.graphviz.renderer

import dagger.Binds
import dagger.Module
import dagger.model.Binding
import dagger.model.BindingGraph
import dagger.model.BindingGraph.Node
import dagger.model.BindingKind
import dagger.model.ComponentPath
import dev.arunkumar.dot.dsl.DotGraphBuilder
import dev.arunkumar.dot.dsl.directedGraph
import dev.arunkumar.scabbard.plugin.options.ScabbardOptions
import dev.arunkumar.scabbard.plugin.output.OutputManager
import dev.arunkumar.scabbard.plugin.parser.TypeNameExtractor
import dev.arunkumar.scabbard.plugin.parser.buildLabel
import dev.arunkumar.scabbard.plugin.parser.name
import dev.arunkumar.scabbard.plugin.parser.scopeName
import dev.arunkumar.scabbard.plugin.store.DaggerScopeColors
import java.util.*
import javax.inject.Inject

interface RenderingContext {
  val rootBindingGraph: BindingGraph

  val typeNameExtractor: TypeNameExtractor

  fun isEntryPoint(binding: BindingGraph.MaybeBinding): Boolean
  fun color(binding: Binding): String
  fun nodeId(node: Node): String

  /**
   * @return `true` if both `source` and `target` were already rendered
   *     in current context.
   */
  fun validInContext(source: Node, target: Node): Boolean

  fun scopeColor(scopeName: String): String

  fun nodeLabel(node: Node): String

  fun href(componentNode: BindingGraph.ComponentNode): String

  fun createRootDotGraphBuilder(currentComponentPath: ComponentPath): DotGraphBuilder

  /** The default attributes for root graph. */
  fun DotGraphBuilder.defaultGraphAttributes(currentComponentPath: ComponentPath)
}

@Module
interface GraphVizRenderingModule {
  @Binds
  fun DefaultRenderingContext.bind(): RenderingContext
}

class DefaultRenderingContext
@Inject
constructor(
  override val rootBindingGraph: BindingGraph,
  override val typeNameExtractor: TypeNameExtractor,
  private val scabbardOptions: ScabbardOptions,
  private val daggerScopeColors: DaggerScopeColors,
  private val outputManager: OutputManager
) : RenderingContext {

  override fun isEntryPoint(binding: BindingGraph.MaybeBinding) = rootBindingGraph
    .entryPointBindings()
    .contains(binding)

  override fun color(binding: Binding) = scopeColor(binding.scopeName() ?: "")

  /**
   * Cache of `id` generated for Nodes rendered so far. This
   * is essential to identify nodes on graph for further
   * mutation operations like adding edges or applying styling.
   */
  private val nodeIdCache = mutableMapOf<Node, String>()

  override fun nodeId(node: Node) = nodeIdCache.getOrPut(node) { UUID.randomUUID().toString() }

  /**
   * @return `true` if both `source` and `target` has been already
   *     rendered in current context.
   */
  override fun validInContext(source: Node, target: Node): Boolean {
    return nodeIdCache.containsKey(source) && nodeIdCache.containsKey(target)
  }

  override fun scopeColor(scopeName: String): String = daggerScopeColors[scopeName]

  override fun nodeLabel(node: Node) = node.run {
    when (this) {
      is Binding -> {
        try {
          val qualifier = key().qualifier().orElse(null)?.let { annotationMirror ->
            typeNameExtractor.extractName(annotationMirror)
          }

          var name = typeNameExtractor.extractName(key().type())

          val scopeName = scopeName()
          val isSubComponentCreator = kind() == BindingKind.SUBCOMPONENT_CREATOR

          key().multibindingContributionIdentifier().ifPresent { identifier ->
            name = typeNameExtractor.extractName(identifier)
          }

          buildLabel(
            name,
            qualifier,
            scopeName,
            isSubComponentCreator
          )
        } catch (e: Exception) {
          e.printStackTrace()
          "Errored $this"
        }
      }
      is BindingGraph.ComponentNode -> {
        val name = typeNameExtractor.extractName(componentPath().currentComponent().asType())
        val scopeName =
          scopes().takeIf { it.isNotEmpty() }?.joinToString(separator = "|") { it.name }
        buildLabel(name = name, scopeName = scopeName)
      }
      else -> toString()
    }
  }

  override fun href(componentNode: BindingGraph.ComponentNode): String {
    return outputManager.outputFileNameFor(
      scabbardOptions.outputImageFormat,
      componentNode.componentPath().currentComponent(),
      rootBindingGraph.isFullBindingGraph
    )
  }

  override fun createRootDotGraphBuilder(currentComponentPath: ComponentPath): DotGraphBuilder {
    return directedGraph(currentComponentPath.toString()) {
      defaultGraphAttributes(currentComponentPath)
    }
  }

  /** The default attributes for root graph. */
  override fun DotGraphBuilder.defaultGraphAttributes(currentComponentPath: ComponentPath) {
    graphAttributes {
      "rankdir" `=` "LR"
      "labeljust" `=` "l"
      "label" `=` typeNameExtractor.extractName(currentComponentPath)
      "pad" `=` 0.2
      "compound" `=` true
    }
    nodeAttributes {
      "shape" `=` "rectangle"
      "style" `=` "filled"
      "color" `=` "turquoise"
    }
  }
}
