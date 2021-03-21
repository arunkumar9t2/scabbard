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

  private lateinit var simpleComponentGeneratedText: String
  private lateinit var subComponentGeneratedText: String

  @Before
  fun setup() {
    simpleComponentGeneratedText = generatedDot<SimpleComponent>()
    subComponentGeneratedText = generatedDot<SimpleSubComponent>()
  }

  @Test
  fun `assert subcomponent hierarchy is set as label`() {
    assertThat(subComponentGeneratedText).contains("label=\"SubComponentDefaultAttributesTest.SimpleComponent → SubComponentDefaultAttributesTest.SimpleSubComponent\"")
  }

  @Test
  fun `assert parent component's subcomponent cluster has hrefs to subcomponent generated files`() {
    assertThat(simpleComponentGeneratedText).contains("href=\"dev.arunkumar.scabbard.plugin.SubComponentDefaultAttributesTest.SimpleSubComponent.png\"")
  }
}
