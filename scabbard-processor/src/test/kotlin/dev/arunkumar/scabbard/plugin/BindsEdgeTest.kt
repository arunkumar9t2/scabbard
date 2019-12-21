package dev.arunkumar.scabbard.plugin

import com.google.common.truth.Truth.assertThat
import dagger.Binds
import dagger.Component
import dagger.Module
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import javax.inject.Inject
import javax.inject.Singleton

@RunWith(JUnit4::class)
class BindsEdgeTest {

  interface SuperType

  class Implementation @Inject constructor() : SuperType

  class Node @Inject constructor(val superType: SuperType)
  @Module
  interface BindsModule {
    @Binds
    fun bindsSuperType(implementation: Implementation): SuperType
  }

  @Singleton
  @Component(modules = [BindsModule::class])
  interface SimpleComponent {
    fun node(): Node
  }

  private lateinit var generatedGraph: String

  @Before
  fun setup() {
    generatedGraph = SimpleComponent::class.java.generatedDotFile().readText()
  }


  @Test
  fun `assert edge to binding node from supertype to implmentation has correct attributes`() {
    assertThat(generatedGraph).contains("[label=\"dev.arunkumar.scabbard.plugin.BindsEdgeTest.SuperType\", color=\"turquoise\"]")
    assertThat(generatedGraph).contains("[label=\"dev.arunkumar.scabbard.plugin.BindsEdgeTest.Implementation\", color=\"turquoise\"]")
    assertThat(generatedGraph).contains("[style=\"dotted\", label=\"delegates\"]")
  }
}