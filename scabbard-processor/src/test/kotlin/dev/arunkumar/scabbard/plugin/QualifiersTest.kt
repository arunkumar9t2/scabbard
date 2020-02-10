package dev.arunkumar.scabbard.plugin

import com.google.common.truth.Truth.assertThat
import dagger.Component
import dagger.Module
import dagger.Provides
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@RunWith(JUnit4::class)
class QualifiersTest {

  @Qualifier
  @MustBeDocumented
  annotation class SimpleQualifier

  @Qualifier
  @MustBeDocumented
  annotation class ComplexQualifier(val someStringValue: String)

  class Node

  @Module
  object QualifiersModule {
    @Provides
    @SimpleQualifier
    fun simpleNode() = Node()

    @Provides
    @Named("node")
    fun namedNode() = Node()

    @Provides
    @ComplexQualifier("simple")
    fun complexNode() = Node()
  }

  @Singleton
  @Component(modules = [QualifiersModule::class])
  interface SimpleComponent {
    @SimpleQualifier
    fun node(): Node

    @Named("node")
    fun namedNode(): Node

    @ComplexQualifier("simple")
    fun complexNode(): Node
  }

  private lateinit var generatedGraphText: String

  @Before
  fun setup() {
    generatedGraphText = SimpleComponent::class.java.generatedDotFile().readText()
  }

  @Test
  fun `assert binding has qualifier prefixed followed by a new line`() {
    assertThat(generatedGraphText).contains("label=\"@QualifiersTest.SimpleQualifier\\nQualifiersTest.Node\"")
  }

  @Test
  fun `assert binding has named qualifier prefixed followed by a new line`() {
    assertThat(generatedGraphText).contains("label=\"@Named(\\\"node\\\")\\nQualifiersTest.Node\"")
  }

  @Test
  fun `assert binding has complex qualifier rendered with values followed by a new line`() {
    assertThat(generatedGraphText).contains("label=\"@QualifiersTest.ComplexQualifier(someStringValue=\\\"simple\\\")\\nQualifiersTest.Node\"")
  }
}