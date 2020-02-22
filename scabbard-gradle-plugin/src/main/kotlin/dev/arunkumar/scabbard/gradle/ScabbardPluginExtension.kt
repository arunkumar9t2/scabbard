package dev.arunkumar.scabbard.gradle

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.invoke
import org.gradle.util.Configurable
import org.gradle.util.ConfigureUtil

/**
 * Scabbard plugin extension that configures the project.
 */
open class ScabbardPluginExtension(
  val project: Project,
  private val objectFactory: ObjectFactory,
  private val extensionConfiguredAction: Action<ScabbardPluginExtension>
) : Configurable<ScabbardPluginExtension> {

  /**
   * Control whether scabbard is enabled or not
   */
  open var enabled: Boolean = true

  /**
   * By default, scabbard does not fail the build when any error occurs in scabbard's processor. Setting
   * this property to `true` will change that behaviour to fail on any error for debugging purposes
   */
  open var failOnError: Boolean = false

  /**
   * Flag to control if fully qualified names should be used everywhere in the graph. Default value
   * is `false`
   */
  open var qualifiedNames: Boolean = false

  /**
   * Configures Dagger processor to do full graph validation which processes each `@Module`, `@Component`
   * and `@Subcomponent`. This enables visualization of missing bindings and generates graphs for
   * `@Module` too.
   */
  open var fullBindingGraphValidation: Boolean = false

  /**
   * The output image format that scabbard generates. Supported values are [OutputFormat.PNG]
   * or [OutputFormat.SVG]
   */
  open var outputFormat: String = OutputFormat.PNG

  /**
   * Implement [Configurable.configure] to receive callback when Gradle configures the extension.
   * For more details, see [https://github.com/arunkumar9t2/scabbard/issues/15]
   */
  override fun configure(closure: Closure<*>): ScabbardPluginExtension {
    ConfigureUtil.configureSelf(closure, this)
    extensionConfiguredAction(this)
    return this
  }
}