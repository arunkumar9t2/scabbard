package dev.arunkumar.scabbard.gradle

open class ScabbardExtension(
  var enabled: Boolean = true,
  var singleGraph: Boolean = false,
  var failOnError: Boolean = true
)