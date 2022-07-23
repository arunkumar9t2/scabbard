package dev.arunkumar.scabbard.gradle

import dev.arunkumar.scabbard.gradle.common.ScabbardBaseTest
import dev.arunkumar.scabbard.gradle.options.CompilerProperty
import dev.arunkumar.scabbard.gradle.options.FAIL_ON_ERROR
import dev.arunkumar.scabbard.gradle.options.FULL_GRAPH_VALIDATION
import org.gradle.api.Action
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExtensionPropertiesTest : ScabbardBaseTest() {

  private val emptyEnabledStateChangeAction = Action<Boolean> {}
  private val emptyCompilerPropertyChangeAction = Action<CompilerProperty<*>> { }

  @Test
  fun `assert enabled property delegate notifies plugin when extension is configured`() {
    project.extensions.create(
      ScabbardGradlePlugin.SCABBARD,
      ScabbardPluginExtension::class.java,
      project,
      Action<Boolean> {
        assertTrue("Received updated extension property", this)
      },
      emptyCompilerPropertyChangeAction
    ).run {
      enabled = true
    }
  }

  @Test
  fun `assert compiler property delegate notifies plugin only when extension property actually changes`() {
    project.extensions.create(
      ScabbardGradlePlugin.SCABBARD,
      ScabbardPluginExtension::class.java,
      project,
      emptyEnabledStateChangeAction,
      Action<CompilerProperty<*>> {
        throw IllegalStateException("Assertion failed")
      }
    ).run {
      failOnError = false // Default value of failOnError is false, configuring it here should not
      // trigger the observable
    }
  }

  @Test
  fun `assert compiler property changes are notified to plugin when it is configured`() {
    project.extensions.create(
      ScabbardGradlePlugin.SCABBARD,
      ScabbardPluginExtension::class.java,
      project,
      emptyEnabledStateChangeAction,
      Action<CompilerProperty<*>> {
        assertEquals("Compiler property change name matches", FAIL_ON_ERROR.name, this.name)
        assertEquals("Compiler property change value matches", true, this.value as Boolean)
      }
    ).run {
      failOnError = true
    }
  }

  @Test
  fun `assert compiler property changes are mapped to another value when mapCompilerProperty is used`() {
    project.extensions.create(
      ScabbardGradlePlugin.SCABBARD,
      ScabbardPluginExtension::class.java,
      project,
      emptyEnabledStateChangeAction,
      Action<CompilerProperty<*>> {
        assertEquals("Compiler property change name matches", FULL_GRAPH_VALIDATION.name, this.name)
        assertEquals(
          "Compiler property change value matches",
          "WARNING",
          this.value // Even though boolean is the type, it should be mapped to `WARNING`
        )
      }
    ).run {
      fullBindingGraphValidation = true
    }
  }
}
