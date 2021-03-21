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

  class NodeC @Inject constructor()

  class NodeA @Inject constructor(val nodeC: NodeC)

  @Singleton
  class NodeB @Inject constructor(private val nodeA: NodeA)

  @Singleton
  @Component
  interface SimpleComponent {
    fun nodeB(): NodeB
  }

  @Singleton
  @Component
  interface EmptyDependencyGraphSubComponent {
    fun nodeC(): NodeC
  }

  private lateinit var simpleComponentGraph: MutableGraph
  private lateinit var simpleComponentDot: String
  private lateinit var emptyDependencyGraphSubComponentGraph: MutableGraph

  @Before
  fun setup() {
    simpleComponentGraph = generatedGraph<SimpleComponent>()
    simpleComponentDot = generatedDot<SimpleComponent>()
    emptyDependencyGraphSubComponentGraph = generatedGraph<EmptyDependencyGraphSubComponent>()
  }

  @Test
  fun `assert dependency graph is rendered in different cluster`() {
    val dependencyGraph = simpleComponentGraph.graphs().firstOrNull() { it.name() == "Dependency Graph" }
    assertThat(dependencyGraph).isNotNull()
  }

  @Test
  fun `assert dependency graph nodes have default attributes applied`() {
    val dependencyGraph = simpleComponentGraph.graphs().firstOrNull() { it.name() == "Dependency Graph" }
    val dependencyGraphNodes = dependencyGraph!!.nodes()
    assertThat(dependencyGraphNodes).hasSize(2)
    assertThat(simpleComponentDot).contains(" graph [labeljust=\"l\", label=\"Dependency Graph\"]")
    // Label and default color
    assertThat(simpleComponentDot).contains("[label=\"DependencyGraphTest.NodeA\", color=\"turquoise\"]")
  }

  @Test
  fun `assert dependency graph cluster is not rendered when dependency nodes are empty`() {
    assertThat(simpleComponentGraph.graphs().none { it.name() == "Dependency Graph" })
  }
}
