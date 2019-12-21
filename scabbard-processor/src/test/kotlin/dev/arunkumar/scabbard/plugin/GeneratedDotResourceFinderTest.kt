package dev.arunkumar.scabbard.plugin

import com.google.common.truth.Truth.assertThat
import dagger.Component
import dagger.Module
import dagger.Provides
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import javax.inject.Inject

@RunWith(JUnit4::class)
class GeneratedDotResourceFinderTest {

  @Component(modules = [StringModule::class])
  interface StringComponent {
    fun string(): String
  }

  @Module
  object StringModule {
    @Provides
    fun string() = "string"
  }

  class Simple @Inject constructor()

  @Component
  interface SimpleComponent {
    fun simple(): Simple
  }

  @Test
  fun `test dot file is generated as resource`() {
    val stringComponentDot = StringComponent::class.java.generatedDotFile()
    assertThat(stringComponentDot.exists()).isTrue()
    assertThat(stringComponentDot.readText()).contains("digraph")
    val simpleComponentDot = SimpleComponent::class.java.generatedDotFile()
    assertThat(simpleComponentDot.exists()).isTrue()
    assertThat(simpleComponentDot.readText()).contains("digraph")
  }
}