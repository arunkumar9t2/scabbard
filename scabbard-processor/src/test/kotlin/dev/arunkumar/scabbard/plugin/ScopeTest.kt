package dev.arunkumar.scabbard.plugin

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
class ScopeTest {

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
  class AnotherSubcomponentNode @Inject constructor()

  @SubScope
  class SubComponentNode
  @Inject
  constructor(
    val nodeB: NodeB,
    private val anotherSubcomponentNode: AnotherSubcomponentNode
  )

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
  fun `test nodes present in a scope have different but consistent colors set`() {
    // TODO
  }
}