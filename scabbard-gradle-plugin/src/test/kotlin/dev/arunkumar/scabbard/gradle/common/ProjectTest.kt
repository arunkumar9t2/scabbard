package dev.arunkumar.scabbard.gradle.common

import dev.arunkumar.scabbard.gradle.SCABBARD
import dev.arunkumar.scabbard.gradle.ScabbardExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before

abstract class ProjectTest {
  lateinit var project: Project

  protected fun prepareScabbardExtension(block: ScabbardExtension.() -> Unit) {
    block(project.extensions.create(SCABBARD))
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