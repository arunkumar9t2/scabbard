package dev.arunkumar.scabbard.gradle.common

import dev.arunkumar.scabbard.gradle.DefaultScabbardSpec
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before

abstract class ProjectTest {
  lateinit var project: Project

  internal fun prepareScabbardExtension(block: DefaultScabbardSpec.() -> Unit = {}): DefaultScabbardSpec {
    return DefaultScabbardSpec(project).apply(block)
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