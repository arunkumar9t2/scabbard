package dev.arunkumar.scabbard.plugin.processor.graphviz.renderer

import com.google.common.truth.Truth.assertThat
import dagger.Component
import dagger.Subcomponent
import dev.arunkumar.scabbard.plugin.generatedDot
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import javax.inject.Inject
import javax.inject.Scope
import javax.inject.Singleton

@RunWith(JUnit4::class)
class InheritedBindingsTest {

  class ParentNodeA @Inject constructor()

  @Singleton
  @Component
  interface ParentComponent {
    fun parentNodeA(): ParentNodeA
    fun childComponentFactory(): ChildComponent.Factory
  }

  class ChildNode
  @Inject
  constructor(
    private val parentNodeA: ParentNodeA
  )

  @Scope
  annotation class ChildScope

  class ChildNodeB @Inject constructor()

  @ChildScope
  @Subcomponent
  interface ChildComponent {
    fun childNode(): ChildNode
    fun anotherChildComponentFactory(): AnotherChildComponent.Factory

    @Subcomponent.Factory
    interface Factory {
      fun create(): ChildComponent
    }
  }

  @Scope
  annotation class AnotherChildScope

  @AnotherChildScope
  class AnotherChildNode
  @Inject
  constructor(
    private val childNode: ChildNode,
    private val parentNodeA: ParentNodeA,
    private val childNodeB: ChildNodeB
  )

  @AnotherChildScope
  @Subcomponent
  interface AnotherChildComponent {
    fun anotherChildNode(): AnotherChildNode

    @Subcomponent.Factory
    interface Factory {
      fun create(): AnotherChildComponent
    }
  }

  private lateinit var generatedAnotherChildComponentDot: String

  @Before
  fun setup() {
    generatedAnotherChildComponentDot = generatedDot<AnotherChildComponent>()
  }

  @Test
  fun `test bindings from inherited components are rendered in their own cluster`() {
    assertThat(generatedAnotherChildComponentDot).contains("subgraph \"cluster_InheritedBindingsTest.ParentComponent → InheritedBindingsTest.ChildComponent\"")
    assertThat(generatedAnotherChildComponentDot).contains("subgraph \"cluster_InheritedBindingsTest.ParentComponent → InheritedBindingsTest.ChildComponent → InheritedBindingsTest.AnotherChildComponent\"")
  }

  @Test
  fun `assert inherited bindings cluster default graph attributes`() {
    // Href, label, style, color
    assertThat(generatedAnotherChildComponentDot)
      .contains(
        "graph [labeljust=\"c\", " +
          "label=\"Inherited from InheritedBindingsTest.ChildComponent\\n@ChildScope\", " +
          "style=\"dashed\", " +
          "href=\"dev.arunkumar.scabbard.plugin.processor.graphviz.renderer.InheritedBindingsTest.ChildComponent.png\", " +
          "color=\"aquamarine\"]"
      )
    assertThat(generatedAnotherChildComponentDot)
      .contains(
        "graph [labeljust=\"c\", " +
          "label=\"Inherited from InheritedBindingsTest.AnotherChildComponent\\n@AnotherChildScope\", " +
          "style=\"dashed\", " +
          "href=\"dev.arunkumar.scabbard.plugin.processor.graphviz.renderer.InheritedBindingsTest.AnotherChildComponent.png\", " +
          "color=\"bisque\"]"
      )
  }
}
