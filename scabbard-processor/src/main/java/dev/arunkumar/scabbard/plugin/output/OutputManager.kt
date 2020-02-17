package dev.arunkumar.scabbard.plugin.output

import com.github.kittinunf.result.Result
import javax.lang.model.element.TypeElement

/**
 * Output files abstraction to create instance of [OutputFiles]
 */
interface OutputManager {
  /**
   * Create output files for the [currentComponent]
   *
   * @param currentComponent the component for which files should be created
   * @param isFullGraph Whether the current graph is a full binding graph.
   */
  fun createOutputFiles(
    currentComponent: TypeElement,
    isFullGraph: Boolean = false
  ): Result<OutputFiles, Exception>
}