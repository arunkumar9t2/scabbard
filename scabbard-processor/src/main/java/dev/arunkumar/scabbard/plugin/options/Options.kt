package dev.arunkumar.scabbard.plugin.options

import dev.arunkumar.scabbard.plugin.options.SupportedOptions.*

data class ScabbardOptions(
  val singleGraph: Boolean = false,
  val failOnError: Boolean = false,
  val qualifiedNames: Boolean = false
)

enum class SupportedOptions(val key: String) {
  SINGLE_GRAPH("scabbard.singleGraph"),
  FAIL_ON_ERROR("scabbard.failOnError"),
  QUALIFIED_NAMES("scabbard.qualifiedNames")
}

val SUPPORTED_OPTIONS = values().map { it.key }.toSet()

private fun Map<String, String>.booleanValue(key: String): Boolean {
  return containsKey(key) && get(key)?.toBoolean() == true
}

fun parseOptions(options: Map<String, String>): ScabbardOptions {
  val isSingleFile = options.booleanValue(SINGLE_GRAPH.key)
  val isFailOnError = options.booleanValue(FAIL_ON_ERROR.key)
  val qualifiedNames = options.booleanValue(QUALIFIED_NAMES.key)
  return ScabbardOptions(
    singleGraph = isSingleFile,
    failOnError = isFailOnError,
    qualifiedNames = qualifiedNames
  )
}