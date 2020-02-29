package dev.arunkumar.scabbard.gradle.output

/**
 * The output format of the generated images.
 */
object OutputFormat {
  const val SVG = "svg"
  const val PNG = "png"

  /**
   * Parse the given `value` to one of the supported extensions.
   *
   * @param value the extension format
   * @throws IllegalArgumentException when an unsupported format is given.
   */
  fun parse(value: String): String {
    if (value == SVG || value == PNG) {
      return value
    } else {
      throw IllegalArgumentException("Illegal output format: $value. Supported formats are 'png' or 'svg")
    }
  }
}