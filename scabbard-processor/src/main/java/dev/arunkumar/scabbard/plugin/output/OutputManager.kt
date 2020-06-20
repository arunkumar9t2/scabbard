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
   * Return the output file name, taking into account the format, component name and it if is a
   * full graph.
   *
   * @param format The extension of the generated file.
   * @param currentComponent the component for which files should be created.
   * @param isFullGraph Whether the current graph is a full binding graph.
   * @param isComponentTree Whether the output is for component tree.
   */
  fun outputFileNameFor(
    format: Format,
    currentComponent: TypeElement,
    isFullGraph: Boolean = false,
    isComponentTree: Boolean = false
  ): String

  /**
   * Create output files for the [currentComponent]
   *
   * @param format The extension of the generated file.
   * @param currentComponent the component for which files should be created.
   * @param isFullGraph Whether the current graph is a full binding graph.
   * @param isComponentTree Whether the output is for component tree.
   */
  fun createOutputFiles(
    format: Format,
    currentComponent: TypeElement,
    isFullGraph: Boolean = false,
    isComponentTree: Boolean = false
  ): Result<FileObject, Exception>
}

/**
 * OutputManager implementation backed by a [Filer]
 */
interface FilerOutputManager : OutputManager {
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
    const val TREE_GRAPH_PREFIX = "tree_"
  }

  private fun Filer.safeCreate(fileName: String) =
    Result.of<FileObject, Exception> {
      createResource(
        StandardLocation.CLASS_OUTPUT,
        SCABBARD_PACKAGE,
        fileName
      )
    }

  override fun outputFileNameFor(
    format: OutputManager.Format,
    currentComponent: TypeElement,
    isFullGraph: Boolean,
    isComponentTree: Boolean
  ): String {
    val componentName = ClassName.get(currentComponent)
    val packageName = componentName.packageName()
    val componentSimpleNames = componentName.simpleNames().joinToString(".")
    val name = "$packageName.$componentSimpleNames".replace("$", ".")
    val prefix = when {
      isFullGraph -> FULL_GRAPH_PREFIX
      isComponentTree -> TREE_GRAPH_PREFIX
      else -> ""
    }
    return "$prefix$name.${format.extension}"
  }

  override fun createOutputFiles(
    format: OutputManager.Format,
    currentComponent: TypeElement,
    isFullGraph: Boolean,
    isComponentTree: Boolean
  ): Result<FileObject, Exception> {
    val fileName = outputFileNameFor(format, currentComponent, isFullGraph, isComponentTree)
    return filer.safeCreate(fileName)
  }
}


