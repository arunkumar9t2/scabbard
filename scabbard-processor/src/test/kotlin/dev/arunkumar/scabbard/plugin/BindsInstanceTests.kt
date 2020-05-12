package dev.arunkumar.scabbard.plugin

import com.google.common.truth.Truth.assertThat
import dagger.BindsInstance
import dagger.Component
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import javax.inject.Inject

@RunWith(JUnit4::class)
class BindsInstanceTests {

  class BoundInstanceNode

  class Node @Inject constructor(private val boundInstanceNode: BoundInstanceNode)

  @Component
  interface SimpleComponent {
    fun node(): Node

    @Component.Factory
    interface Factory {
      fun create(@BindsInstance boundInstanceNode: BoundInstanceNode): SimpleComponent
    }
  }

  private lateinit var generatedGraph: String

  @Before
  fun setup() {
    generatedGraph = SimpleComponent::class.java.generatedDotFile().readText()
  }

  @Test
  fun `assert bound instances have diamond shape`() {
    assertThat(generatedGraph).contains("shape=\"parallelogram\"")
  }
}