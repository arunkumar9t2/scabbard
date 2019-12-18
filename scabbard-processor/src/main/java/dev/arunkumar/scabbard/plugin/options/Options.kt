package dev.arunkumar.scabbard.plugin.options

import dev.arunkumar.scabbard.plugin.options.SupportedOptions.FAIL_ON_ERROR
import dev.arunkumar.scabbard.plugin.options.SupportedOptions.SINGLE_GRAPH

data class ScabbardOptions(
  val singleGraph: Boolean = false,
  val failOnError: Boolean = false
)

enum class SupportedOptions(val key: String) {
  SINGLE_GRAPH("scabbard.singleGraph"),
  FAIL_ON_ERROR("scabbard.failOnError")
}

val SUPPORTED_OPTIONS = SupportedOptions.values().map { it.key }.toSet()

private fun Map<String, String>.booleanValue(key: String): Boolean {
  return containsKey(key) && get(key)?.toBoolean() == true
}

fun parseOptions(options: Map<String, String>): ScabbardOptions {
  val isSingleFile = options.booleanValue(SINGLE_GRAPH.key)
  val isFailOnError = options.booleanValue(FAIL_ON_ERROR.key)
  return ScabbardOptions(
    singleGraph = isSingleFile,
    failOnError = isFailOnError
  )
}