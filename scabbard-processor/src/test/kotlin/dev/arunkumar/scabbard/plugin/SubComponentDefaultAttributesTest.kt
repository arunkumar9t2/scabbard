package dev.arunkumar.scabbard.plugin

import com.google.common.truth.Truth.assertThat
import dagger.Component
import dagger.Subcomponent
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton

@RunWith(JUnit4::class)
class SubComponentDefaultAttributesTest {

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

  private lateinit var generatedText: String

  @Before
  fun setup() {
    generatedText = SimpleSubComponent::class.java.generatedDotFile().readText()
  }

  @Test
  fun `test subcomponent hierarchy is set as label`() {
    assertThat(generatedText).contains("label=\"SubComponentDefaultAttributesTest.SimpleComponent → SubComponentDefaultAttributesTest.SimpleSubComponent\"")
  }
}