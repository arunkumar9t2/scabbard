package dev.arunkumar.scabbard.gradle.processor

import dev.arunkumar.scabbard.gradle.PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardExtension
import dev.arunkumar.scabbard.gradle.projectmeta.ANNOTATION_PROCESSOR
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertTrue
import org.junit.Test

class ScabbardProcessorManagerTest {

  private fun Project.hasScabbard(isKoltin: Boolean = true): Boolean {
    return project.configurations
      .filter { configuration ->
        when {
          isKoltin -> configuration.name == KAPT
          else -> configuration.name == ANNOTATION_PROCESSOR
        }
      }.flatMap { it.dependencies }.any { dep ->
        "${dep.group}:${dep.name}:${dep.version}" == SCABBARD_PROCESSOR
      }
  }

  @Test
  fun `when no config are present assert scabbard processor is not added`() {
    val project = ProjectBuilder.builder().build()
    project.plugins.apply {
      apply(PLUGIN_ID)
    }
    assertTrue("Scabbard applied", !project.hasScabbard())
  }

  @Test
  fun `when kapt is present assert scabbard processor is added by default (kapt)`() {
    val project = ProjectBuilder.builder().build()
    project.plugins.apply {
      apply("kotlin")
      apply("kotlin-kapt")
      apply(PLUGIN_ID)
    }
    assertTrue("Scabbard applied", project.hasScabbard())
  }

  @Test
  fun `when kapt added and scabbard extension disabled assert scabbard processor is not added`() {
    val project = ProjectBuilder.builder().build()
    project.plugins.apply {
      apply("kotlin")
      apply("kotlin-kapt")
      apply(PLUGIN_ID)
    }
    project.extensions.getByType<ScabbardExtension>().apply {
      enabled = false
    }
    project.afterEvaluate {
      assertTrue("Scabbard is not applied", !project.hasScabbard())
    }
  }

  @Test
  fun `when java plugin present assert scabbard processor is added by default (annotationProcessor)`() {
    val project = ProjectBuilder.builder().build()
    project.plugins.apply {
      apply("java")
      apply(PLUGIN_ID)
    }
    assertTrue("Scabbard applied", project.hasScabbard(isKoltin = false))
  }

  @Test
  fun `when java plugin present and scabbard extension disabled assert scabbard processor is not added`() {
    val project = ProjectBuilder.builder().build()
    project.plugins.apply {
      apply("java")
      apply(PLUGIN_ID)
    }
    project.extensions.getByType<ScabbardExtension>().apply {
      enabled = false
    }
    project.afterEvaluate {
      assertTrue("Scabbard is not applied", !project.hasScabbard(isKoltin = false))
    }
  }

  @Test
  fun `when both java and kotlin are present assert scabbard processor is added only in kapt config`() {
    val project = ProjectBuilder.builder().build()
    project.plugins.apply {
      apply("java")
      apply("kotlin")
      apply("kotlin-kapt")
      apply(PLUGIN_ID)
    }
    project.extensions.getByType<ScabbardExtension>().apply {
      enabled = false
    }
    project.afterEvaluate {
      assertTrue("Scabbard is not applied", !project.hasScabbard(isKoltin = true))
    }
  }
}