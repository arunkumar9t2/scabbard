package dev.arunkumar.scabbard.plugin.options

data class ScabbardOptions(
  val singleGraph: Boolean
)

const val SINGLE_GRAPH = "scabbard.singleGraph"

private fun Map<String, String>.booleanValue(key: String): Boolean {
  return containsKey(key) && get(key)?.toBoolean() == true
}

fun parseOptions(options: Map<String, String>): ScabbardOptions {
  val isSingleFile = options.booleanValue(SINGLE_GRAPH)
  return ScabbardOptions(isSingleFile)
}