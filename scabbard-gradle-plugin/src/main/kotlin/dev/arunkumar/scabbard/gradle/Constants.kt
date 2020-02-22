package dev.arunkumar.scabbard.gradle

object OutputFormat {
  const val SVG = "svg"
  const val PNG = "png"

  fun parse(value: String): String {
    if (value == SVG || value == PNG) {
      return value
    } else {
      throw IllegalArgumentException("Illegal output format: $value. Supported formats are 'png' or 'svg")
    }
  }
}