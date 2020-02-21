package dev.arunkumar.scabbard.plugin.output

import com.github.kittinunf.result.Result
import com.squareup.javapoet.ClassName
import dev.arunkumar.scabbard.plugin.di.ProcessorScope
import javax.annotation.processing.Filer
import javax.inject.Inject
import javax.lang.model.element.TypeElement
import javax.tools.FileObject
import javax.tools.StandardLocation

/**
 * Output files abstraction to create [FileObject] instance for given component and extensions
 */
interface OutputManager {

  enum class Format(val extension: String) {
    DOT("dot"),
    PNG("png"),
    SVG("svg")
  }

  /**
   * Create output files for the [currentComponent]
   *
   * @param currentComponent the component for which files should be created
   * @param isFullGraph Whether the current graph is a full binding graph.
   */
  fun createOutputFiles(
    format: Format,
    currentComponent: TypeElement,
    isFullGraph: Boolean = false
  ): Result<FileObject, Exception>
}

/**
 * OutputManager implementation backed by a [Filer]
 */
interface FilerOutputManager :
  OutputManager {
  val filer: Filer
}

@ProcessorScope
class DefaultOutputManager
@Inject
constructor(
  override val filer: Filer
) : FilerOutputManager {

  companion object {
    const val SCABBARD_PACKAGE = "scabbard"
    const val FULL_GRAPH_PREFIX = "full_"
  }

  private fun Filer.safeCreate(fileName: String) =
    Result.of<FileObject, Exception> {
      createResource(
        StandardLocation.CLASS_OUTPUT,
        SCABBARD_PACKAGE,
        fileName
      )
    }

  override fun createOutputFiles(
    format: OutputManager.Format,
    currentComponent: TypeElement,
    isFullGraph: Boolean
  ): Result<FileObject, Exception> {
    val componentName = ClassName.get(currentComponent)
    val packageName = componentName.packageName()
    val componentSimpleNames = componentName.simpleNames().joinToString(".")
    val name = "$packageName.$componentSimpleNames".replace("$", ".")
    val prefix = if (isFullGraph) FULL_GRAPH_PREFIX else ""
    val fileName = "$prefix$name"
    return filer.safeCreate("$fileName.${format.extension}")
  }
}


