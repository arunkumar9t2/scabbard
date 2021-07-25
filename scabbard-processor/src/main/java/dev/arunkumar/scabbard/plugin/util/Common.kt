/*
 * Copyright 2021 Arunkumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.arunkumar.scabbard.plugin.util

import dev.arunkumar.scabbard.plugin.options.ScabbardOptions
import dev.arunkumar.scabbard.plugin.processor.BindingGraphProcessor

/**
 * Function that executes the actual processing operations usually by a [BindingGraphProcessor]. The
 * execution of the [block] is customized by [scabbardOptions]
 */
inline fun processingBlock(
  scabbardOptions: ScabbardOptions = ScabbardOptions(),
  crossinline block: () -> Unit
) = scabbardOptions.exceptionHandler(block)

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
    throw RuntimeException("Scabbard processor failed", exception)
  }
}
