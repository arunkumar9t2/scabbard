package dev.arunkumar.scabbard.plugin.processor.graphviz

import com.google.common.truth.Truth.assertThat
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet
import dev.arunkumar.scabbard.plugin.generatedDot
import dev.arunkumar.scabbard.plugin.generatedGraph
import guru.nidi.graphviz.attribute.Label
import guru.nidi.graphviz.model.MutableGraph
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import javax.inject.Inject
import javax.inject.Singleton

@RunWith(JUnit4::class)
class MultibindingsAttributesTest {

  class NodeA @Inject constructor(@JvmSuppressWildcards private val set: Set<String>)

  @Singleton
  class NodeB @Inject constructor(private val nodeA: NodeA)

  @Module
  object MultibindingsModulde {
    @Provides
    @ElementsIntoSet
    @JvmSuppressWildcards
    fun someStrings(): Set<String> = hashSetOf("NodeA", "NodeB")
  }

  @Singleton
  @Component(modules = [MultibindingsModulde::class])
  interface SimpleComponent {
    fun nodeB(): NodeB
  }

  private lateinit var generatedGraph: MutableGraph
  private lateinit var generatedText: String

  @Before
  fun setup() {
    generatedGraph = generatedGraph<SimpleComponent>()
    generatedText = generatedDot<SimpleComponent>()
  }

  @Test
  fun `assert multibindings in dependency graph are rendered in different cluster`() {
    val dependencyGraph = generatedGraph.graphs().firstOrNull() { it.name() == "Dependency Graph" }
    assertThat(dependencyGraph).isNotNull()
    val multibindingsCluster = dependencyGraph!!.graphs().firstOrNull()
    assertThat(multibindingsCluster).isNotNull()
  }

  @Test
  fun `assert multibindings nodes have correct attributes`() {
    val multibindingsCluster = generatedGraph.graphs()
      .firstOrNull() { it.name() == "Dependency Graph" }
      ?.graphs()
      ?.firstOrNull()

    assertThat(multibindingsCluster!!.name()).isEqualTo("Set<String>")
    val multibindingsAttrs = multibindingsCluster.graphAttrs()
    assertThat(multibindingsAttrs["label"]).isEqualTo(Label.of("Set<String>"))
    assertThat(multibindingsAttrs["labeljust"]).isEqualTo("c")
    assertThat(multibindingsAttrs["style"]).isEqualTo("rounded")

    assertThat(generatedText).contains("[label=\"Set<String>\", color=\"turquoise\", shape=\"tab\"]")
    assertThat(generatedText).contains("[label=\"MultibindingsAttributesTest.NodeA\", color=\"turquoise\"]")
  }
}
