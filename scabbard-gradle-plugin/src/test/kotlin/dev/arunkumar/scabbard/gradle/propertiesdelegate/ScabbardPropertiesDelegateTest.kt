package dev.arunkumar.scabbard.gradle.propertiesdelegate

import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.PLUGIN_ID
import dev.arunkumar.scabbard.gradle.common.ProjectTest
import dev.arunkumar.scabbard.gradle.propertiesdelegate.ScabbardPropertiesDelegate.Companion.DAGGER_FULL_BINDING_GRAPH_VALIDATION
import dev.arunkumar.scabbard.gradle.propertiesdelegate.ScabbardPropertiesDelegate.Companion.FAIL_ON_ERROR
import org.gradle.api.Project
import org.gradle.api.tasks.compile.CompileOptions
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.junit.Assert.assertTrue
import org.junit.Test

class ScabbardPropertiesDelegateTest : ProjectTest() {

  private fun Project.kaptOptions(): Map<String, String> {
    return project.extensions.findByType<KaptExtension>()
      ?.getAdditionalArguments(this, null, null)
      ?: emptyMap()
  }

  private fun Project.javacOptions(compileOptions: (CompileOptions) -> Unit) {
    val javaCTasks = project.tasks.withType<JavaCompile>()
    assertTrue("JavaC tasks are present", javaCTasks.isNotEmpty())
    javaCTasks.onEach { javaCompile -> compileOptions(javaCompile.options) }
  }

  @Test
  fun `when kapt is present is assert extension properties are delegated to kapt`() {
    project.plugins.apply {
      apply("kotlin")
      apply("kotlin-kapt")
      apply(PLUGIN_ID)
    }

    ScabbardPropertiesDelegate(prepareScabbardExtension {
      failOnError = true
    }).delegate()

    project.kaptOptions().let { options ->
      assertTrue(
        "Kapt arguments are added",
        options.containsKey(FAIL_ON_ERROR) && options.containsValue("true")
      )
    }
  }

  @Test
  fun `for pure java projects assert extension properties are delegated to JavaCompile tasks`() {
    project.plugins.apply {
      apply("java")
      apply(PLUGIN_ID)
    }

    ScabbardPropertiesDelegate(prepareScabbardExtension {
      failOnError = true
    }).delegate()

    project.javacOptions { options ->
      assertTrue(
        "JavaC Arguments are added",
        options.compilerArgs.contains("-A$FAIL_ON_ERROR=true")
      )
    }
  }

  @Test
  fun `assert fullBindingGraphValidation property is forwarded to kapt`() {
    project.plugins.apply {
      apply("kotlin")
      apply("kotlin-kapt")
      apply(PLUGIN_ID)
    }

    ScabbardPropertiesDelegate(prepareScabbardExtension {
      fullGraphValidation = true
    }).delegate()

    assertTrue(
      "Binding graph validation key is present",
      project.kaptOptions().contains(DAGGER_FULL_BINDING_GRAPH_VALIDATION)
    )
  }

  @Test
  fun `for java projects assert fullBindingGraphValidation property is forwarded to kapt and javac`() {
    project.plugins.apply {
      apply("java")
      apply(PLUGIN_ID)
    }

    ScabbardPropertiesDelegate(prepareScabbardExtension {
      fullGraphValidation = true
    }).delegate()

    project.javacOptions { options ->
      assertTrue(
        "JavaC binding graph validation key is added",
        options.compilerArgs.contains("-A$DAGGER_FULL_BINDING_GRAPH_VALIDATION")
      )
    }
  }
}