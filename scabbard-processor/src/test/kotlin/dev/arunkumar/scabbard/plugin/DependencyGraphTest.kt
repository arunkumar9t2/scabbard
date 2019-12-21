package dev.arunkumar.scabbard.plugin

import com.google.common.truth.Truth.assertThat
import dagger.Component
import guru.nidi.graphviz.attribute.Label
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

  lateinit var generatedGraph: MutableGraph

  @Before
  fun setup() {
    generatedGraph = SimpleComponent::class.java.parsedGraph()
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

    // The first once is usually the component node
    val componentNode = dependencyGraphNodes.first()
    assertThat(componentNode.attrs()["shape"]).isEqualTo("point")
    assertThat(componentNode.attrs()["style"]).isEqualTo("invis")

    val nodeB = dependencyGraphNodes.toList()[1]
    assertThat(nodeB.attrs()["label"]).isEqualTo(Label.of(NodeA::class.java.name))
    assertThat(nodeB.attrs()["color"]).isEqualTo("turquoise")
  }
}