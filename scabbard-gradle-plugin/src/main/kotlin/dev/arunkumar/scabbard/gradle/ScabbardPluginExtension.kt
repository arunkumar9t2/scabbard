package dev.arunkumar.scabbard.gradle

import dev.arunkumar.scabbard.gradle.propertiesdelegate.*
import dev.arunkumar.scabbard.gradle.util.enabledProperty
import org.gradle.api.Action
import org.gradle.api.Project

/**
 * Scabbard plugin extension that configures the project.
 */
open class ScabbardPluginExtension(
  val project: Project,
  internal val onEnabled: Action<Boolean>,
  internal val onCompilerPropertyChanged: Action<CompilerProperty<*>>
) {

  /**
   * Control whether scabbard is enabled or not
   */
  open var enabled by enabledProperty()

  /**
   * By default, scabbard does not fail the build when any error occurs in scabbard's processor. Setting
   * this property to `true` will change that behaviour to fail on any error for debugging purposes
   */
  open var failOnError by compilerProperty(FAIL_ON_ERROR)

  /**
   * Flag to control if fully qualified names should be used everywhere in the graph. Default value
   * is `false`
   */
  open var qualifiedNames by compilerProperty(QUALIFIED_NAMES)
  /**
   * Configures Dagger processor to do full graph validation which processes each `@Module`, `@Component`
   * and `@Subcomponent`. This enables visualization of missing bindings and generates graphs for
   * `@Module` too.
   */
  open var fullBindingGraphValidation by mapCompilerProperty(
    compilerProperty = FULL_GRAPH_VALIDATION,
    valueMapper = FULL_GRAPH_VALIDATION_MAPPER
  )

  /**
   * The output image format that scabbard generates. Supported values are [OutputFormat.PNG]
   * or [OutputFormat.SVG]
   */
  open var outputFormat by mapCompilerProperty(
    OUTPUT_FORMAT,
    valueMapper = OutputFormat::parse
  )

  /**
   * Executes the given [block] if the plugin is `enabled` with the extension as the receiver.
   */
  internal fun ifEnabled(block: ScabbardPluginExtension.() -> Unit) {
    if (enabled) {
      block(this)
    }
  }
}