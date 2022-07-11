package dev.arunkumar.scabbard.gradle.common

import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.JAVA_LIBRARY_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.KAPT_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.KOTLIN_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin.Companion.SCABBARD_PLUGIN_ID
import dev.arunkumar.scabbard.gradle.ScabbardPluginExtension
import dev.arunkumar.scabbard.gradle.processor.DAGGER_COMPILER
import dev.arunkumar.scabbard.gradle.processor.DAGGER_GROUP
import dev.arunkumar.scabbard.gradle.processor.configName
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.repositories
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before

abstract class ScabbardBaseTest {
  /**
   * Current gradle [Project] instance. This instance is setup in
   * [setUp] and cleaned up after the current unit of test finishes.
   */
  protected lateinit var project: Project

  /**
   * Get the [ScabbardPluginExtension] instance associated with the
   * plugin. Modifying this extension mimics gradle configuration phase
   * and as per default logic, should have effect on [project] instance.
   */
  protected val scabbardExtension: ScabbardPluginExtension
    get() = project.extensions.getByType()

  internal fun scabbardExtension(
    block: ScabbardPluginExtension.() -> Unit
  ) = scabbardExtension.apply(block)

  internal fun Project.baseSetup() {
    repositories {
      google()
      mavenCentral()
    }
  }

  /** Setup the [project] instance as a kotlin project. */
  internal fun Project.setupAsKotlin() {
    baseSetup()
    plugins.apply {
      apply(KOTLIN_PLUGIN_ID)
      apply(KAPT_PLUGIN_ID)
      apply(SCABBARD_PLUGIN_ID)
    }
  }

  /** Setup the [project] instance as a pure java project. */
  internal fun Project.setupAsJava() {
    baseSetup()
    plugins.apply {
      apply(JAVA_LIBRARY_PLUGIN_ID)
      apply(SCABBARD_PLUGIN_ID)
    }
  }

  internal fun Project.addDagger(isKapt: Boolean = false) {
    dependencies {
      add(configName(isKapt), "$DAGGER_GROUP:$DAGGER_COMPILER:+")
    }
  }

  @Before
  fun setUp() {
    project = ProjectBuilder.builder().build()
  }

  /** Delete all plugins from [project] instance */
  @After
  fun tearDown() {
    project.plugins.removeIf { true }
  }
}
