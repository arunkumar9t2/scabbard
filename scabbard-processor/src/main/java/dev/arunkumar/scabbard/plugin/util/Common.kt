package dev.arunkumar.scabbard.plugin.util

import dev.arunkumar.scabbard.plugin.BindingGraphProcessor
import dev.arunkumar.scabbard.plugin.options.ScabbardOptions

/**
 * Function that executes the actual processing operations usually by a [BindingGraphProcessor]. The
 * execution of the [block] is customized by [scabbardOptions]
 */
inline fun processingBlock(
  scabbardOptions: ScabbardOptions = ScabbardOptions(),
  crossinline block: () -> Unit
) = scabbardOptions.exceptionHandler {
  block()
}

/**
 * Wraps the given [block] in a `try catch` block and handles exception based on [ScabbardOptions.failOnError]
 *
 * @see ScabbardOptions.handleException
 */
inline fun ScabbardOptions.exceptionHandler(block: () -> Unit) {
  try {
    block()
  } catch (e: Exception) {
    handleException(e)
  }
}

/**
 * Extensions to handle exceptions based on options specified by the user.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun ScabbardOptions.handleException(exception: Exception) {
  if (failOnError) {
    throw RuntimeException("Scabbard processor failed")
  } else {
    exception.printStackTrace()
  }
}