package dev.arunkumar.scabbard.gradle

import com.google.common.truth.Truth.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ScabbardExtensionTest {

  @get:Rule
  val testProjectDir = TemporaryFolder()

  private lateinit var gradleRunner: GradleRunner

  @Before
  fun setUp() {
    gradleRunner = GradleRunner.create()
      .withProjectDir(testProjectDir.root)
      .withPluginClasspath()
  }

  @Test
  fun `assert scabbard extension is available`() {
    testProjectDir.newFile("build.gradle").apply {
      writeText(
        """
        | plugins {
        |   id "scabbard.gradle"
        | }
        | 
        | scabbard {
        |    enabled true
        |    failOnError true
        |    fullBindingGraphValidation true
        |    qualifiedNames false
        |    outputFormat "svg"
        | }
      """.trimMargin()
      )
    }
    val result = gradleRunner
      .withArguments(":help")
      .build()
    assertThat(result.task(":help")?.outcome == TaskOutcome.SUCCESS)
  }
}