package dev.arunkumar.scabbard.gradle.processor

import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.JAVA_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.KAPT_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.KOTLIN_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.SCABBARD_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.common.ScabbardBaseTest
import dev.arunkumar.scabbard.gradle.projectmeta.ANNOTATION_PROCESSOR
import dev.arunkumar.scabbard.gradle.projectmeta.VersionCalculator
import org.gradle.api.Project
import org.junit.Assert.assertTrue
import org.junit.Test

class ScabbardProcessorManagerTest : ScabbardBaseTest() {

  private fun Project.hasScabbardProcessor(isKoltin: Boolean = true): Boolean {
    return project.configurations
      .filter { configuration ->
        when {
          isKoltin -> configuration.name == KAPT
          else -> configuration.name == ANNOTATION_PROCESSOR
        }
      }.flatMap { it.dependencies }.any { dep ->
        val version = VersionCalculator(project).calculate()
        "${dep.group}:${dep.name}:${dep.version}" == SCABBARD_PROCESSOR_FORMAT.format(version)
      }
  }

  @Test
  fun `when no config are present assert scabbard processor is not added`() {
    project.plugins.apply {
      apply(SCABBARD_PLUGIN_ID)
    }
    assertTrue("Scabbard applied", !project.hasScabbardProcessor())
  }

  @Test
  fun `when kapt is present assert scabbard processor is added by default (kapt)`() {
    project.setupAsKotlin()
    ScabbardProcessorManager(scabbardExtension()).manage()
    assertTrue("Scabbard applied", project.hasScabbardProcessor())
  }

  @Test
  fun `when kapt added and scabbard extension disabled assert scabbard processor is not added`() {
    project.setupAsKotlin()
    ScabbardProcessorManager(scabbardExtension {
      enabled = false
    }).manage()
    assertTrue("Scabbard is not applied", !project.hasScabbardProcessor())
  }

  @Test
  fun `when java plugin present assert scabbard processor is added by default (annotationProcessor)`() {
    project.setupAsJava()
    ScabbardProcessorManager(scabbardExtension()).manage()
    assertTrue("Scabbard applied", project.hasScabbardProcessor(isKoltin = false))
  }

  @Test
  fun `when java plugin present and scabbard extension disabled assert scabbard processor is not added`() {
    project.setupAsJava()

    ScabbardProcessorManager(scabbardExtension {
      enabled = false
    }).manage()

    assertTrue("Scabbard is not applied", !project.hasScabbardProcessor(isKoltin = false))
  }

  @Test
  fun `when both java and kotlin are present assert scabbard processor is added only in kapt config`() {
    project.plugins.apply {
      apply(JAVA_PLUGIN_ID)
      apply(KOTLIN_PLUGIN_ID)
      apply(KAPT_PLUGIN_ID)
      apply(SCABBARD_PLUGIN_ID)
    }

    ScabbardProcessorManager(scabbardExtension {
      enabled = false
    }).manage()

    assertTrue("Scabbard is not applied", !project.hasScabbardProcessor(isKoltin = false))
  }
}