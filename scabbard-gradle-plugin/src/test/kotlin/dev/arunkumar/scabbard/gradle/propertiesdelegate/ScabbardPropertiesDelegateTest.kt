package dev.arunkumar.scabbard.gradle.propertiesdelegate

import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.PLUGIN_ID
import dev.arunkumar.scabbard.gradle.common.ProjectTest
import dev.arunkumar.scabbard.gradle.propertiesdelegate.ScabbardPropertiesDelegate.Companion.FAIL_ON_ERROR
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.junit.Assert.assertTrue
import org.junit.Test

class ScabbardPropertiesDelegateTest : ProjectTest() {

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

    project.extensions.findByType<KaptExtension>()?.apply {
      val options = getAdditionalArguments(project, null, null)
      assertTrue(
        "Kapt arguments are added",
        options.containsKey(FAIL_ON_ERROR) && options.containsValue("true")
      )
    }
  }

  @Test
  fun `when pure java module assert extension properties are delegated to JavaCompile tasks`() {
    project.plugins.apply {
      apply("java")
      apply(PLUGIN_ID)
    }

    ScabbardPropertiesDelegate(prepareScabbardExtension {
      failOnError = true
    }).delegate()

    project.extensions.findByType<KaptExtension>()?.apply {
      val options = getAdditionalArguments(project, null, null)
      assertTrue(options.containsKey(FAIL_ON_ERROR) && options.containsValue("true"))
    }
    val javaCTasks = project.tasks.withType<JavaCompile>()
    assertTrue("JavaC tasks are present", javaCTasks.isNotEmpty())
    javaCTasks.onEach { javaCompile ->
      assertTrue(
        "JavaC Arguments are added",
        javaCompile.options.compilerArgs.contains("-A$FAIL_ON_ERROR=true")
      )
    }
  }
}