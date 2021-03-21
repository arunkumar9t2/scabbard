package dev.arunkumar.scabbard.plugin

import com.google.common.truth.Truth.assertThat
import dagger.Component
import dagger.Module
import dagger.Provides
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import javax.inject.Inject

@RunWith(JUnit4::class)
class FullBindingGraphTest {

  class UnUsedNode @Inject constructor()
  class Node

  @Module
  object SimpleModule {
    @Provides
    fun node() = Node()
  }

  @Component(modules = [SimpleModule::class])
  interface SimpleComponent {
    fun node(): Node

    @Component.Factory
    interface Factory {
      fun create(): SimpleComponent
    }
  }

  private lateinit var simpleComponentDotFile: File
  private lateinit var fullSimpleComponentDotFile: File
  private lateinit var simpleModuleDotFile: File

  @Before
  fun setup() {
    simpleComponentDotFile = SimpleComponent::class.java.generatedDotFile()
    fullSimpleComponentDotFile = SimpleComponent::class.java.generatedFullDotFile()
    simpleModuleDotFile = SimpleModule::class.java.generatedFullDotFile()
  }

  @Test
  fun `assert full binding graph validation succeeds when fail on error is enabled`() {
    assertThat(simpleComponentDotFile.exists()).isTrue()
  }

  @Test
  fun `assert full binding graph is generated as separate file with a prefix`() {
    assertThat(fullSimpleComponentDotFile.exists()).isTrue()
  }

  @Test
  fun `assert graph image is generated for simple module`() {
    assertThat(simpleModuleDotFile.exists()).isTrue()
  }
}
