package dev.arunkumar.scabbard.gradle.compilerproperties

import dev.arunkumar.scabbard.gradle.common.ScabbardBaseTest
import dev.arunkumar.scabbard.gradle.options.DAGGER_FULL_GRAPH_VALIDATION
import dev.arunkumar.scabbard.gradle.options.FAIL_ON_ERROR
import dev.arunkumar.scabbard.gradle.options.OUTPUT_FORMAT
import dev.arunkumar.scabbard.gradle.options.QUALIFIED_NAMES
import dev.arunkumar.scabbard.gradle.output.OutputFormat.SVG
import dev.arunkumar.scabbard.gradle.processor.ANNOTATION_PROCESSOR_CONFIG
import org.gradle.api.Project
import org.gradle.api.tasks.compile.CompileOptions
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test

/**
 * Set of tests around how Scabbard plugin receives user preference and
 * configures both Kotlin and Java projects
 */
@Ignore("To be fixed as part of refactor")
class CompilerPropertiesTest : ScabbardBaseTest() {

  private fun Project.kaptOptions(): Map<String, String> {
    return project.extensions.findByType<KaptExtension>()
      ?.getAdditionalArguments(this, null, null)
      ?: emptyMap()
  }

  private fun Project.javacOptions(compileOptions: (CompileOptions) -> Unit) {
    // Force a resolution of annotationProcessor
    configurations.findByName(ANNOTATION_PROCESSOR_CONFIG)?.resolve()
    val javaCTasks = project.tasks.withType<JavaCompile>()
    assertTrue("Javac tasks are present", javaCTasks.isNotEmpty())
    javaCTasks.onEach { javaCompile -> compileOptions(javaCompile.options) }
  }

  @Test
  fun `assert when kapt is present extension properties are delegated to kapt`() {
    project.setupAsKotlin()

    scabbardExtension {
      enabled = true
      failOnError = true
      qualifiedNames = true
    }

    project.kaptOptions().let { options ->
      assertTrue(
        "Fail on error flag added",
        options.containsKey(FAIL_ON_ERROR.name) && options.containsValue("true")
      )
      assertTrue(
        "Qualified names flag added",
        options.containsKey(QUALIFIED_NAMES.name) && options.containsValue("true")
      )
    }
  }

  @Test
  fun `assert for pure java projects extension properties are delegated to JavaCompile tasks`() {
    project.setupAsJava()
    project.addDagger()

    scabbardExtension {
      enabled = true
      failOnError = true
      qualifiedNames = true
    }

    project.javacOptions { options ->
      assertTrue(
        "Fail on error flag  added",
        options.compilerArgs.contains("-A${FAIL_ON_ERROR.name}=true")
      )
      assertTrue(
        "Qualified names flag added",
        options.compilerArgs.contains("-A${QUALIFIED_NAMES.name}=true")
      )
    }
  }

  @Test
  fun `assert fullBindingGraphValidation property is forwarded to kapt`() {
    project.setupAsKotlin()
    project.addDagger()

    scabbardExtension {
      enabled = true
      fullBindingGraphValidation = true
    }

    assertTrue(
      "Binding graph validation key is present",
      project.kaptOptions().contains(DAGGER_FULL_GRAPH_VALIDATION.name)
    )
  }

  @Test
  fun `assert for java projects fullBindingGraphValidation property is forwarded to javac`() {
    project.setupAsJava()
    project.addDagger()

    scabbardExtension {
      enabled = true
      fullBindingGraphValidation = true
    }

    project.javacOptions { options ->
      assertTrue(
        "Javac binding graph validation key is added",
        options.compilerArgs.contains("-A${DAGGER_FULL_GRAPH_VALIDATION.name}=WARNING")
      )
    }
  }

  @Test
  fun `assert output format is forwarded to kapt`() {
    project.setupAsKotlin()

    scabbardExtension {
      enabled = true
      outputFormat = SVG
    }

    assertTrue(
      "Output format is set to svg",
      project.kaptOptions().containsKey(OUTPUT_FORMAT.name) &&
        project.kaptOptions().containsValue(SVG)
    )
  }

  @Test
  fun `assert for java projects output format is forwarded to javac`() {
    project.setupAsJava()
    project.addDagger()

    scabbardExtension {
      enabled = true
      outputFormat = SVG
    }

    project.javacOptions { options ->
      assertTrue(
        "Output format is set to svg",
        options.compilerArgs.contains("-A${OUTPUT_FORMAT.name}=$SVG")
      )
    }
  }

  @Test
  fun `assert compiler properties are not added when dagger is not there`() {
    project.setupAsJava()

    scabbardExtension {
      enabled = true
      outputFormat = SVG
    }

    project.javacOptions { options ->
      assertFalse(
        "Output format is not set",
        options.compilerArgs.contains("-A${OUTPUT_FORMAT.name}=$SVG")
      )
    }
  }
}
