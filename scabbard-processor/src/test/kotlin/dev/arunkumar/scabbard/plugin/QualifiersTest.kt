package dev.arunkumar.scabbard.plugin

import com.google.common.truth.Truth.assertThat
import dagger.Component
import dagger.Module
import dagger.Provides
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import javax.inject.Qualifier
import javax.inject.Singleton

@RunWith(JUnit4::class)
class QualifiersTest {

  @Qualifier
  @MustBeDocumented
  annotation class SimpleQualifier

  class Node

  @Module
  object QualifiersModule {
    @Provides
    @SimpleQualifier
    fun providesNode() = Node()
  }

  @Singleton
  @Component(modules = [QualifiersModule::class])
  interface SimpleComponent {
    @SimpleQualifier
    fun node(): Node
  }

  private lateinit var generatedGraphText: String

  @Before
  fun setup() {
    generatedGraphText = SimpleComponent::class.java.generatedDotFile().readText()
  }

  @Test
  fun `assert binding has qualifier prefixed about binding name`() {
    assertThat(generatedGraphText).contains("label=\"@QualifiersTest.SimpleQualifier\\nQualifiersTest.Node\"")
  }
}