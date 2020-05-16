package dev.arunkumar.scabbard.plugin.processor.graphviz.renderer

import com.google.common.truth.Truth.assertThat
import dagger.Component
import dagger.Subcomponent
import dev.arunkumar.scabbard.plugin.generatedGraph
import guru.nidi.graphviz.model.MutableGraph
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import javax.inject.Inject
import javax.inject.Scope
import javax.inject.Singleton

@RunWith(JUnit4::class)
class EdgeRendererTest {

  class NodeA @Inject constructor()

  @Singleton
  class NodeB @Inject constructor(private val nodeA: NodeA)

  @Singleton
  @Component
  interface SimpleComponent {
    fun nodeB(): NodeB
    fun subComponentFactory(): SimpleSubComponent.Factory
  }

  @Scope
  annotation class SubScope

  class SubComponentNodeA @Inject constructor()

  @SubScope
  class SubComponentNode
  @Inject
  constructor(val nodeB: NodeB, val subComponentNode: SubComponentNodeA)

  @SubScope
  @Subcomponent
  interface SimpleSubComponent {
    fun subcomponentNode(): SubComponentNode

    @Subcomponent.Factory
    interface Factory {
      fun create(): SimpleSubComponent
    }
  }

  private lateinit var simpleSubComponentGraph: MutableGraph

  @Before
  fun setUp() {
    simpleSubComponentGraph = generatedGraph<SimpleSubComponent>()
  }

  @Test
  fun `test edge renderer only renders edges for nodes laid out so far`() {
    val dependencyNodes = simpleSubComponentGraph.graphs().first { it.name() == "Dependency Graph" }.nodes()
    assertThat(dependencyNodes).hasSize(2)
    assertThat(simpleSubComponentGraph.graphs().first { it.name() == "Entry Points" }.nodes()).hasSize(1)
  }
}