package dev.arunkumar.scabbard.plugin

import com.google.common.truth.Truth.assertThat
import dagger.Component
import dagger.Subcomponent
import guru.nidi.graphviz.model.MutableGraph
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton

@RunWith(JUnit4::class)
class SubcomponentCreatorBindingEdgeTest {

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

  private lateinit var generatedGraph: MutableGraph
  private lateinit var generatedText: String


  @Before
  fun setup() {
    generatedGraph = SimpleComponent::class.java.parsedGraph()
    generatedText = SimpleComponent::class.java.generatedDotFile().readText()
  }

  @Test
  fun `assert edge between component and subcomponent creator is rendered with dashed lines and label`() {
    assertThat(generatedText).contains(" subgraph \"cluster_Subcomponents\" {")
    assertThat(generatedText).contains(
      "[shape=\"component\", " +
          "label=\"SubcomponentCreatorBindingEdgeTest.SimpleSubComponent.Factory\\n\\nSubcomponent Creator\"," +
          " penwidth=\"2\"]"
    )
    assertThat(generatedText).contains("[style=\"dashed\", xlabel=\"subcomponent\"]")
    assertThat(generatedText).contains("[label=\"SubcomponentCreatorBindingEdgeTest.NodeA\", color=\"turquoise\"]")
  }
}