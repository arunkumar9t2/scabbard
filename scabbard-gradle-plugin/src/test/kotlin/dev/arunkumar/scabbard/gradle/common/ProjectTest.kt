package dev.arunkumar.scabbard.gradle.common

import dev.arunkumar.scabbard.gradle.ScabbardExtension
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.SCABBARD
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before

abstract class ProjectTest {
  lateinit var project: Project

  protected fun prepareScabbardExtension(block: ScabbardExtension.() -> Unit = {}): ScabbardExtension {
    return (project.extensions.getByName(SCABBARD) as ScabbardExtension).also { block(it) }
  }

  @Before
  fun setUp() {
    project = ProjectBuilder.builder().build()
  }

  @After
  fun tearDown() {
    project.plugins.removeIf { true }
  }
}