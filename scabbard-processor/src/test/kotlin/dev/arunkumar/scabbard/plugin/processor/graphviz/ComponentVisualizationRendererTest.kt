package dev.arunkumar.scabbard.plugin.processor.graphviz

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import dagger.Component
import dagger.Subcomponent
import dev.arunkumar.scabbard.plugin.generatedDot
import dev.arunkumar.scabbard.plugin.generatedGraph
import guru.nidi.graphviz.model.MutableGraph
import guru.nidi.graphviz.model.MutableNode
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import javax.inject.Inject
import javax.inject.Singleton

@RunWith(JUnit4::class)
class ComponentVisualizationRendererTest {
  class NodeA @Inject constructor()

  @Singleton
  class NodeB @Inject constructor(private val nodeA: NodeA)

  @Singleton
  @Component
  interface SimpleComponent {
    fun nodeB(): NodeB
    fun subComponentFactory(): SimpleSubComponent.Factory
  }

  class SubComponentNode
  @Inject
  constructor(val nodeB: NodeB)

  @Subcomponent
  interface SimpleSubComponent {
    fun subcomponentNode(): SubComponentNode

    @Subcomponent.Factory
    interface Factory {
      fun create(): SimpleSubComponent
    }
  }

  private lateinit var generatedGraph: MutableGraph
  private lateinit var generatedText: String

  @Before
  fun setup() {
    generatedGraph = generatedGraph<SimpleSubComponent>()
    generatedText = generatedDot<SimpleSubComponent>()
  }

  @Test
  fun `assert for subcomponents, only it's own visible nodes are rendered and rest are filtered`() {
    // Notes: This is affected by RenderingContext implementation. If RenderingContext's was not correctly constructed for
    // each component, then renderer will render nodes on to the graph.

    // We can't accurately test with ids alone so we rely on other things below
    // Check no of nodes.
    assertThat(generatedGraph.nodes()).apply {
      hasSize(2)
    }
    // Collect nodes inside entry point and dependency graph
    val validNodeMap = generatedGraph.graphs().flatMap(MutableGraph::nodes).associateBy(MutableNode::name)
    // Ensure all nodes are only from entry point or dep graph
    assertWithMessage("All nodes are valid nodes")
      .that(generatedGraph.nodes().all { validNodeMap.containsKey(it.name()) })
      .isTrue()
  }
}
