package dev.arunkumar.scabbard.plugin

import com.google.common.truth.Truth.assertThat
import dagger.Component
import guru.nidi.graphviz.model.MutableGraph
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import javax.inject.Inject
import javax.inject.Singleton


@RunWith(JUnit4::class)
class RootComponentDefaultAttributesTest {

  class NodeA @Inject constructor()
  @Singleton
  class NodeB @Inject constructor(private val nodeA: NodeA)

  @Singleton
  @Component
  interface SimpleComponent {
    fun nodeB(): NodeB
  }

  private lateinit var generatedGraph: MutableGraph
  private lateinit var generatedText: String

  @Before
  fun setup() {
    generatedGraph = SimpleComponent::class.java.parsedGraph()
    generatedText = SimpleComponent::class.java.generatedDotFile().readText()
  }

  @Test
  fun `assert root component has default graph attributes set`() {
    // Test graph name
    assertThat(generatedText)
      .contains("digraph \"dev.arunkumar.scabbard.plugin.RootComponentDefaultAttributesTest.SimpleComponent\" {")
    // Default graph attributes
    assertThat(generatedText).contains("graph [rankdir=\"LR\", labeljust=\"l\", label=\"RootComponentDefaultAttributesTest.SimpleComponent\", pad=\"0.2\", compound=\"true\"]")
    // Default node attributes
    assertThat(generatedText).contains(" node [shape=\"rectangle\", style=\"filled\", color=\"turquoise\"]")
  }
}