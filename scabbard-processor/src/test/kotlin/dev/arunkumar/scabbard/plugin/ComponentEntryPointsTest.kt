package dev.arunkumar.scabbard.plugin

import com.google.common.truth.Truth.assertThat
import dagger.Component
import dagger.Module
import dagger.Provides
import guru.nidi.graphviz.model.MutableGraph
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ComponentEntryPointsTest {

  class MemberInjectionTarget

  @Component(modules = [StringModule::class])
  interface StringComponent {
    fun string(): String
    fun inject(memberInjectionTarget: MemberInjectionTarget)
  }

  @Module
  object StringModule {
    @Provides
    fun string() = "string"
  }

  private lateinit var generatedDot: String
  private lateinit var generatedGraph: MutableGraph

  @Before
  fun setup() {
    generatedDot = StringComponent::class.java.generatedDotFile().readText()
    generatedGraph = StringComponent::class.java.parsedGraph()
  }

  @Test
  fun `assert entry points are rendered as separate cluster`() {
    assertThat(generatedGraph.graphs()).hasSize(3)
    val entryPoints = generatedGraph.graphs().first()
    assertThat(entryPoints.name() == "Entry Points")
  }

  @Test
  fun `assert entry points cluster nodes have correct attributes set`() {
    val entryPoints = generatedGraph.graphs().first()
    assertThat(entryPoints.nodes()).hasSize(2)
    assertThat(generatedDot).contains("shape=\"component\", label=\"String\", penwidth=\"2\"")
  }

  @Test
  fun `assert member injection target have correct labels set`() {
    assertThat(generatedDot).contains("inject (ComponentEntryPointsTest.MemberInjectionTarget)")
  }
}