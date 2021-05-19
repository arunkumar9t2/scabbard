package dev.arunkumar.scabbard.plugin.processor.graphviz

import com.google.common.truth.Truth.assertThat
import dagger.Component
import dagger.Subcomponent
import dev.arunkumar.scabbard.plugin.generatedComponentTreeDotFile
import guru.nidi.graphviz.parse.Parser
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton

@RunWith(JUnit4::class)
class ComponentTreeRendererTest {
  class NodeA @Inject constructor()

  @Singleton
  class NodeB @Inject constructor(private val nodeA: NodeA)

  @Singleton
  @Component
  interface SimpleComponent {
    fun nodeB(): NodeB
    fun subComponentFactory(): SimpleSubComponent.Factory
  }

  @Qualifier
  annotation class SubScope

  @SubScope
  class SubComponentNode
  @Inject
  constructor(val nodeB: NodeB)

  @SubScope
  @Subcomponent
  interface SimpleSubComponent {
    fun subcomponentNode(): SubComponentNode

    @Subcomponent.Factory
    interface Factory {
      fun create(): SimpleSubComponent
    }
  }

  private lateinit var simpleComponentTreeDotFile: File
  private lateinit var simpleSubComponent: File
  private lateinit var componentTreeDotContents: String

  @Before
  fun setup() {
    simpleComponentTreeDotFile = generatedComponentTreeDotFile<SimpleComponent>()
    simpleSubComponent = generatedComponentTreeDotFile<SimpleSubComponent>()
    componentTreeDotContents = simpleComponentTreeDotFile.readText()
  }

  @Test
  fun `assert component tree is generated only for root module`() {
    assertThat(simpleComponentTreeDotFile.exists()).isTrue()
    assertThat(simpleSubComponent.exists()).isFalse()
  }

  @Test
  fun `test default graph attributes on component tree dot file`() {
    assertThat(componentTreeDotContents).contains("digraph \"dev.arunkumar.scabbard.plugin.processor.graphviz.ComponentTreeRendererTest.SimpleComponent\"")
    assertThat(componentTreeDotContents).contains("graph [rankdir=\"TB\", label=\"ComponentTreeRendererTest.SimpleComponent\", compound=\"true\", labeljust=\"l\", pad=\"0.2\"")
    assertThat(componentTreeDotContents).contains("node [shape=\"rectangle\", style=\"filled\", color=\"turquoise\"]")
    val graph = Parser().read(simpleComponentTreeDotFile)
    assertThat(graph.nodes()).hasSize(2)
    assertThat(graph.nodes().flatMap { it.links() }).hasSize(1)
    assertThat(graph.graphs()).hasSize(0)
  }
}
