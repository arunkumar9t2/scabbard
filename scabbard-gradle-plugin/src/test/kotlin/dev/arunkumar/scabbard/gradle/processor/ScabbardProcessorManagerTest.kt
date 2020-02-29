package dev.arunkumar.scabbard.gradle.processor

import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.JAVA_LIBRARY_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.KAPT_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.KOTLIN_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.SCABBARD_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.common.ScabbardBaseTest
import org.gradle.api.Project
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Set of tests around how Scabbard's annotation processor dependency is added to project based on
 * project structure and user preference.
 */
class ScabbardProcessorManagerTest : ScabbardBaseTest() {

  private fun Project.hasScabbardProcessor(isKoltin: Boolean = true): Boolean {
    return project.configurations
      .filter { configuration ->
        when {
          isKoltin -> configuration.name == KAPT_CONFIG
          else -> configuration.name == ANNOTATION_PROCESSOR_CONFIG
        }
      }.flatMap { it.dependencies }.any { dep ->
        "${dep.group}:${dep.name}:${dep.version}" == SCABBARD_PROCESSOR_DEPENDENCY
      }
  }

  @Test
  fun `assert when no config are present, scabbard processor is not added`() {
    project.plugins.apply {
      apply(SCABBARD_PLUGIN_ID)
    }
    assertTrue("Scabbard applied", !project.hasScabbardProcessor())
  }

  @Test
  fun `assert for kotlin projects, scabbard processor is not added by default (kapt)`() {
    project.setupAsKotlin()
    assertTrue("Scabbard is not applied", !project.hasScabbardProcessor())
  }

  @Test
  fun `assert for kotlin projects and scabbard extension disabled, scabbard processor is not added`() {
    project.setupAsKotlin()
    scabbardExtension {
      enabled = true
    }
    assertTrue("Scabbard is applied on Kapt", project.hasScabbardProcessor())
  }

  @Test
  fun `assert when java plugin present, scabbard processor is not added by default (annotationProcessor)`() {
    project.setupAsJava()
    assertTrue("Scabbard applied", !project.hasScabbardProcessor(isKoltin = false))
  }

  @Test
  fun `assert when java plugin present and scabbard extension disabled assert scabbard processor is not added`() {
    project.setupAsJava()

    scabbardExtension {
      enabled = true
    }

    assertTrue(
      "Scabbard is applied on annotationProcessor",
      project.hasScabbardProcessor(isKoltin = false)
    )
    assertTrue(
      "Scabbard is not applied on kapt",
      !project.hasScabbardProcessor(isKoltin = true)
    )
  }

  @Test
  fun `assert when both java and kotlin are present assert scabbard processor is added only in kapt config`() {
    project.plugins.apply {
      apply(JAVA_LIBRARY_PLUGIN_ID)
      apply(KOTLIN_PLUGIN_ID)
      apply(KAPT_PLUGIN_ID)
      apply(SCABBARD_PLUGIN_ID)
    }
    scabbardExtension {
      enabled = true
    }

    assertTrue(
      "Scabbard is not applied on annotationProcessor",
      !project.hasScabbardProcessor(isKoltin = false)
    )
    assertTrue(
      "Scabbard is applied on kapt",
      project.hasScabbardProcessor(isKoltin = true)
    )
  }

  @Test
  fun `assert when scabbard processor was manually added and in extension it is disabled, the dependency is removed`() {
    project.setupAsKotlin()

    // Add the processor dependency manually
    project.dependencies.add(KAPT_CONFIG, SCABBARD_PROCESSOR_DEPENDENCY)

    scabbardExtension {
      enabled = false
    }
    assertTrue(
      "Scabbard is not applied on kapt",
      !project.hasScabbardProcessor(isKoltin = true)
    )
    assertTrue(
      "Scabbard is not applied on annotationProcessor",
      !project.hasScabbardProcessor(isKoltin = false)
    )
  }
}