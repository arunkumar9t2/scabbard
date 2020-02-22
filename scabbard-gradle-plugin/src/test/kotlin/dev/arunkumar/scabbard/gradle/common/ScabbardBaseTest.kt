package dev.arunkumar.scabbard.gradle.common

import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.JAVA_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.KAPT_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.KOTLIN_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.SCABBARD_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardPluginExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before

abstract class ScabbardBaseTest {

  lateinit var project: Project

  internal fun scabbardExtension(
    block: ScabbardPluginExtension.() -> Unit = {}
  ): ScabbardPluginExtension {
    return ScabbardPluginExtension(project, project.objects, Action { }).apply(block)
  }

  internal fun Project.setupAsKotlin() {
    plugins.apply {
      apply(KOTLIN_PLUGIN_ID)
      apply(KAPT_PLUGIN_ID)
      apply(SCABBARD_PLUGIN_ID)
    }
  }

  internal fun Project.setupAsJava() {
    plugins.apply {
      apply(JAVA_PLUGIN_ID)
      apply(SCABBARD_PLUGIN_ID)
    }
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