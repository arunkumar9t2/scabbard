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

@RunWith(JUnit4::class)
class SvgFormatTests {
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
  private lateinit var simpleComponentSvgFile: File

  @Before
  fun setup() {
    simpleComponentDotFile = SimpleComponent::class.java.generatedDotFile()
    simpleComponentSvgFile = SimpleComponent::class.java.generatedSvgFile()
  }

  @Test
  fun `assert default png file is generated`() {
    assertThat(simpleComponentDotFile.exists())
    assertThat(simpleComponentDotFile.readText().contains("digraph"))
  }

  @Test
  fun `assert svg file is generated`() {
    assertThat(simpleComponentSvgFile.exists())
    assertThat(simpleComponentSvgFile.readText().contains("svg"))
  }
}