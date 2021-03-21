package dev.arunkumar.scabbard.plugin.options

import dev.arunkumar.scabbard.plugin.options.SupportedOptions.*
import dev.arunkumar.scabbard.plugin.output.OutputManager.Format

data class ScabbardOptions(
  val singleGraph: Boolean = false,
  val failOnError: Boolean = false,
  val qualifiedNames: Boolean = false,
  val outputImageFormat: Format = Format.PNG
)

enum class SupportedOptions(val key: String) {
  SINGLE_GRAPH("scabbard.singleGraph"),
  FAIL_ON_ERROR("scabbard.failOnError"),
  QUALIFIED_NAMES("scabbard.qualifiedNames"),
  IMAGE_FORMAT("scabbard.outputFormat")
}

val SUPPORTED_OPTIONS = values().map { it.key }.toSet()

private fun Map<String, String>.booleanValue(key: String): Boolean {
  return containsKey(key) && get(key)?.toBoolean() == true
}

private fun Map<String, String>.parseImageFormat() = try {
  Format.valueOf(this[IMAGE_FORMAT.key]!!.toUpperCase())
} catch (ignored: Exception) {
  Format.PNG
}

fun parseOptions(options: Map<String, String>): ScabbardOptions {
  val isSingleFile = options.booleanValue(SINGLE_GRAPH.key)
  val isFailOnError = options.booleanValue(FAIL_ON_ERROR.key)
  val qualifiedNames = options.booleanValue(QUALIFIED_NAMES.key)
  val outputFormat = options.parseImageFormat()
  return ScabbardOptions(
    singleGraph = isSingleFile,
    failOnError = isFailOnError,
    qualifiedNames = qualifiedNames,
    outputImageFormat = outputFormat
  )
}
