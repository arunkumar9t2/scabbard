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
import javax.inject.Scope
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
    fun unScopedComponentFactory(): UnScopedSubComponent.Factory
  }

  @Scope
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

  class UnScopedSubComponentNode @Inject constructor()

  @Subcomponent
  interface UnScopedSubComponent {
    fun unScopedSubComponentNode(): UnScopedSubComponentNode

    @Subcomponent.Factory
    interface Factory {
      fun create(): UnScopedSubComponent
    }
  }

  private lateinit var simpleComponent: String
  private lateinit var simpleSubcomponent: String
  private lateinit var simpleGraph: MutableGraph

  @Before
  fun setup() {
    simpleComponent = generatedDot<SimpleComponent>()
    simpleSubcomponent = generatedDot<SimpleSubComponent>()
    simpleGraph = generatedGraph<SimpleComponent>()
  }

  @Test
  fun `test nodes present in a scope have different but consistent colors set`() {
    assertThat(simpleSubcomponent)
      .contains("[label=\"@SubScope\\nScopeTest.AnotherSubcomponentNode\", color=\"aquamarine\"]")
  }

  @Test
  fun `test root component links to all subcomponents and subcomponents have colors set on them`() {
    assertThat(simpleComponent)
      .contains("label=\"@SubScope\\nScopeTest.SimpleSubComponent\"")
    assertThat(simpleComponent)
      .contains("color=\"aquamarine\"")
  }

  @Test
  fun `test unscoped subcomponent has the default color set`() {
    val subcomponentAttrs = simpleGraph.graphs()
      .first { it.name() == "Subcomponents" }
      .nodes()
      .first { node ->
        node.attrs()
          .any { it.value.toString().contains(UnScopedSubComponent::class.java.simpleName) }
      }
    assertThat(subcomponentAttrs).hasSize(2)
    assertThat(subcomponentAttrs.none { it.key == "color" })
  }
}