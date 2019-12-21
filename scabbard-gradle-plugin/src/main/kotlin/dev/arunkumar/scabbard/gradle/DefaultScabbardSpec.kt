package dev.arunkumar.scabbard.gradle

import org.gradle.api.Project

interface ScabbardSpec {

  fun enabled(enabled: Boolean)
  // fun singleGraph(enabled: Boolean)
  fun failOnError(failOnError: Boolean)

  /**
   * Configures Dagger processor to do full graph validation which processes each `@Module`, `@Component`
   * and `@Subcomponent`. This enables visualization of missing bindings and generates graphs for
   * `@Module` too. */
  fun fullBindingGraphValidation(enabled: Boolean)
}

open class DefaultScabbardSpec(val project: Project) : ScabbardSpec {

  var isScabbardEnabled = true
  override fun enabled(enabled: Boolean) {
    isScabbardEnabled = enabled
  }

  var failOnError = true
  override fun failOnError(failOnError: Boolean) {
    this.failOnError = failOnError
  }

  var fullGraphValidation = false
  override fun fullBindingGraphValidation(enabled: Boolean) {
    fullGraphValidation = enabled
  }

  inline fun ifEnabled(block: () -> Unit) {
    if (isScabbardEnabled) {
      block()
    }
  }
}