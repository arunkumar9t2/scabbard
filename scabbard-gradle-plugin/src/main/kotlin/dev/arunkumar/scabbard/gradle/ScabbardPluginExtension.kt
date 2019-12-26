package dev.arunkumar.scabbard.gradle

import dev.arunkumar.scabbard.gradle.processor.ScabbardProcessorManager
import dev.arunkumar.scabbard.gradle.propertiesdelegate.ScabbardPropertiesDelegate
import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.util.Configurable
import org.gradle.util.ConfigureUtil.configure

/**
 * Scabbard plugin extension that configures the project.
 */
open class ScabbardPluginExtension(
  private val project: Project
) : Configurable<ScabbardPluginExtension> {

  override fun configure(closure: Closure<*>): ScabbardPluginExtension {
    val scabbardSpec = DefaultScabbardSpec(project)
    configure(closure, scabbardSpec)
    ScabbardProcessorManager(scabbardSpec).manage()
    ScabbardPropertiesDelegate(scabbardSpec).delegate()
    return this
  }
}