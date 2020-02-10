package dev.arunkumar.scabbard.plugin

import com.google.common.truth.Truth.assertThat
import dagger.Component
import dagger.Module
import dagger.Provides
import guru.nidi.graphviz.attribute.Label
import guru.nidi.graphviz.model.MutableGraph
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ComponentEntryPointsTest {

  @Component(modules = [StringModule::class])
  interface StringComponent {
    fun string(): String
  }

  @Module
  object StringModule {
    @Provides
    fun string() = "string"
  }

  lateinit var generatedGraph: MutableGraph

  @Before
  fun setup() {
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
    val stringEntryPoint = entryPoints.nodes().first()
    val attrs = stringEntryPoint.attrs()
    assertThat(attrs["shape"].toString()).contains("component")
    assertThat(attrs["label"]).isEqualTo(Label.of("String"))
    assertThat(attrs["penwidth"].toString()).contains("2")
  }
}