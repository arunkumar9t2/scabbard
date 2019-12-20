package dev.arunkumar.scabbard

import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * A simple functional test for the 'dev.arunkumar.scabbard.greeting' plugin.
 */
class ScabbardGradlePluginPluginFunctionalTest {

  @Test
  fun `can run task`() {
    // Setup the test build
    val projectDir = File("build/functionalTest")
    projectDir.mkdirs()
    projectDir.resolve("settings.gradle").writeText("")
    projectDir.resolve("build.gradle").writeText(
      """
            plugins {
                id('scabbard-gradle-plugin')
            }
        """
    )

    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("greeting")
    runner.withProjectDir(projectDir)
    val result = runner.build()

    // Verify the result
    assertTrue(result.output.contains("Hello from plugin 'dev.arunkumar.scabbard.greeting'"))
  }
}