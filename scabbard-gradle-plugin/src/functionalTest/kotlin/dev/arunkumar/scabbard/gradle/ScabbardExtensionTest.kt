package dev.arunkumar.scabbard.gradle

import com.google.common.truth.Truth.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Test

class ScabbardExtensionTest : ScabbardFunctionalTest() {

  @Test
  fun `assert scabbard extension is available`() {
    projectFile(
      path = "build.gradle",
      content = """
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
    val result = gradleRunner
      .withArguments(":help")
      .build()
    assertThat(result.task(":help")?.outcome == TaskOutcome.SUCCESS)
  }
}
