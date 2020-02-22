package dev.arunkumar.scabbard.gradle

import org.gradle.api.Project

interface ScabbardSpec {

  fun enabled(enabled: Boolean)
  // fun singleGraph(enabled: Boolean)

  fun failOnError(failOnError: Boolean)

  /**
   * Flag to control if fully qualified names should be used everywhere in the graph. Default value
   * is `false`
   */
  fun qualifiedNames(enabled: Boolean)

  /**
   * Configures Dagger processor to do full graph validation which processes each `@Module`, `@Component`
   * and `@Subcomponent`. This enables visualization of missing bindings and generates graphs for
   * `@Module` too. */
  fun fullBindingGraphValidation(enabled: Boolean)
  
  
  /**
   * Specify the output image format. Can be be one of `png` or `svg`.
   */
  fun outputFormat(outputFormat: String)
}

internal open class DefaultScabbardSpec(val project: Project) : ScabbardSpec {

  var isScabbardEnabled = true
  override fun enabled(enabled: Boolean) {
    isScabbardEnabled = enabled
  }

  var failOnError = false
  override fun failOnError(failOnError: Boolean) {
    this.failOnError = failOnError
  }

  var qualifiedNames = false
  override fun qualifiedNames(enabled: Boolean) {
    qualifiedNames = enabled
  }

  var fullGraphValidation = false
  override fun fullBindingGraphValidation(enabled: Boolean) {
    fullGraphValidation = enabled
  }

  var outputFormat: String = "png"
  override fun outputFormat(outputFormat: String) {
    this.outputFormat = outputFormat
  }


  inline fun ifEnabled(block: () -> Unit) {
    if (isScabbardEnabled) {
      block()
    }
  }
}