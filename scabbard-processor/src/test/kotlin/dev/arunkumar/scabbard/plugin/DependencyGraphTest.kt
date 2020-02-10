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
class DependencyGraphTest {

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
  fun `assert dependency graph is rendered in different cluster`() {
    val dependencyGraph = generatedGraph.graphs().firstOrNull() { it.name() == "Dependency Graph" }
    assertThat(dependencyGraph).isNotNull()
  }

  @Test
  fun `assert dependency graph nodes have default attributes applied`() {
    val dependencyGraph = generatedGraph.graphs().firstOrNull() { it.name() == "Dependency Graph" }
    val dependencyGraphNodes = dependencyGraph!!.nodes()
    assertThat(dependencyGraphNodes).hasSize(2)
    assertThat(generatedText).contains(" graph [labeljust=\"l\", label=\"Dependency Graph\"]")
    assertThat(generatedText).contains("[style=\"invis\", shape=\"point\"]") // component node
    // Label and default color
    assertThat(generatedText).contains("[label=\"DependencyGraphTest.NodeA\", color=\"turquoise\"]")
  }
}