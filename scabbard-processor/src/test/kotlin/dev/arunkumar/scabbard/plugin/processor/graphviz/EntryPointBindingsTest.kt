package dev.arunkumar.scabbard.plugin.processor.graphviz

import com.google.common.truth.Truth.assertThat
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dev.arunkumar.scabbard.plugin.generatedDot
import dev.arunkumar.scabbard.plugin.generatedGraph
import guru.nidi.graphviz.model.MutableGraph
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import javax.inject.Inject
import javax.inject.Scope

@RunWith(JUnit4::class)
class EntryPointBindingsTest {

  class MemberInjectionTarget

  @Component(modules = [StringModule::class])
  interface StringComponent {
    fun string(): String
    fun inject(memberInjectionTarget: MemberInjectionTarget)
    fun simpleSubComponent(): SimpleSubcomponent.Factory
  }

  @Module
  object StringModule {
    @Provides
    fun string() = "string"
  }

  @Scope
  annotation class SubScope

  class SimpleSubComponentNodeB @Inject constructor()

  @SubScope
  class SimpleSubComponentNode
  @Inject
  constructor(private val simpleSubComponentNodeB: SimpleSubComponentNodeB)

  @Subcomponent
  @SubScope
  interface SimpleSubcomponent {
    fun simpleSubComponentNodeB(): SimpleSubComponentNodeB
    fun simpleSubComponentNode(): SimpleSubComponentNode

    @Subcomponent.Factory
    interface Factory {
      fun create(): SimpleSubcomponent
    }
  }

  private lateinit var stringComponentDot: String
  private lateinit var stringComponentGraph: MutableGraph
  private lateinit var simpleSubComponentDot: String

  @Before
  fun setup() {
    stringComponentDot = generatedDot<StringComponent>()
    stringComponentGraph = generatedGraph<StringComponent>()
    simpleSubComponentDot = generatedDot<SimpleSubcomponent>()
  }

  @Test
  fun `assert entry points are rendered as separate cluster`() {
    assertThat(stringComponentGraph.graphs()).hasSize(3)
    val entryPoints = stringComponentGraph.graphs().first()
    assertThat(entryPoints.name() == "Entry Points")
  }

  @Test
  fun `assert entry points cluster nodes have correct attributes set`() {
    val entryPointsGraph = stringComponentGraph.graphs().first()
    assertThat(entryPointsGraph.nodes()).hasSize(3)
    // The attributes set on entry points cluster
    assertThat(stringComponentDot).contains("node [shape=\"component\", penwidth=\"2\"]")
  }

  @Test
  fun `assert member injection target have correct labels set`() {
    assertThat(stringComponentDot).contains("label=\"inject (EntryPointBindingsTest.MemberInjectionTarget)\"")
  }

  @Test
  fun `assert entry point nodes are styled similar to dependency graph bindings`() {
    assertThat(simpleSubComponentDot)
      .contains("[label=\"@SubScope\\nEntryPointBindingsTest.SimpleSubComponentNode\", color=\"aquamarine\", shape=\"component\"]")
  }
}
