package dev.arunkumar.scabbard.plugin.parser

import com.google.common.truth.Truth.assertThat
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dev.arunkumar.scabbard.plugin.generatedDotFile
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton

@RunWith(JUnit4::class)
class SimpleNamesTest {

  object NestedInnerClassType {
    object Level1 {
      object Level2 {
        object Level3 {
          class NestedNode @Inject constructor()
        }
      }
    }
  }

  class NodeA @Inject constructor()

  @Singleton
  class NodeB
  @Inject
  constructor(
    private val nodeA: NodeA,
    private val nodes: List<NodeA>,
    private val integer: Int,
    private val double: Double,
    private val char: Char,
    private val boolean: Boolean,
    private val nestedNode: NestedInnerClassType.Level1.Level2.Level3.NestedNode
  )

  @Module
  object SimpleModule {
    @Provides
    fun provideList(nodeA: NodeA): List<NodeA> = listOf(nodeA)

    @Provides
    fun providesInt(): Int = 0

    @Provides
    fun providesDouble(): Double = 0.0

    @Provides
    fun providesChar(): Char = 'a'

    @Provides
    fun providesBool(): Boolean = true

    @Provides
    fun provideNodePair(nodeA: NodeA, nodeB: NodeB): Pair<NodeA, NodeB> = nodeA to nodeB
  }

  @Singleton
  @Component(modules = [SimpleModule::class])
  interface SimpleComponent {
    fun nodeB(): NodeB
    fun nodePair(): Pair<NodeA, NodeB>
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

  private lateinit var generatedText: String

  @Before
  fun setup() {
    generatedText = SimpleComponent::class.java.generatedDotFile().readText()
  }

  @Test
  fun `assert generics type are extracted correctly`() {
    assertThat(generatedText)
      .contains("label=\"List<SimpleNamesTest.NodeA>\"")
    assertThat(generatedText)
      .contains("label=\"Pair<SimpleNamesTest.NodeA, SimpleNamesTest.NodeB>\"")
  }

  @Test
  fun `assert primitive are mapped to boxed types`() {
    assertThat(generatedText)
      .contains("[label=\"Integer\"")
    assertThat(generatedText)
      .contains("[label=\"Double\"")
    assertThat(generatedText)
      .contains("[label=\"Character\"")
    assertThat(generatedText)
      .contains("[label=\"Boolean\"")
  }

  @Test
  fun `assert nested inner class type has max 2 levels in its simple name`() {
    assertThat(generatedText)
      .contains("label=\"Level3.NestedNode\"")
  }
}