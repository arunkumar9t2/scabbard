package dev.arunkumar.scabbard.gradle

import org.gradle.api.Project

interface ScabbardSpec {
  fun enabled(enabled: Boolean)
  // fun singleGraph(enabled: Boolean)
  fun failOnError(failOnError: Boolean)
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

  inline fun ifEnabled(block: () -> Unit) {
    if (isScabbardEnabled) {
      block()
    }
  }
}