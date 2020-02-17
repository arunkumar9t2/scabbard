package dev.arunkumar.scabbard.plugin.output

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.flatMap
import com.github.kittinunf.result.map
import com.squareup.javapoet.ClassName
import dev.arunkumar.scabbard.plugin.di.ProcessorScope
import dev.arunkumar.scabbard.plugin.options.ScabbardOptions
import javax.annotation.processing.Filer
import javax.inject.Inject
import javax.lang.model.element.TypeElement
import javax.tools.FileObject
import javax.tools.StandardLocation.CLASS_OUTPUT

@ProcessorScope
class DefaultOutputManager
@Inject
constructor(
  override val filer: Filer,
  private val scabbardOptions: ScabbardOptions
) : FilerOutputManager {

  companion object {
    const val SCABBARD_PACKAGE = "scabbard"
    const val FULL_GRAPH_PREFIX = "full_"
  }

  private fun Filer.safeCreate(fileName: String) = Result.of<FileObject, Exception> {
    createResource(CLASS_OUTPUT, SCABBARD_PACKAGE, fileName)
  }

  override fun createOutputFiles(
    currentComponent: TypeElement,
    isFullGraph: Boolean
  ): Result<OutputFiles, Exception> {
    val componentName = ClassName.get(currentComponent)
    val packageName = componentName.packageName()
    val componentSimpleNames = componentName.simpleNames().joinToString(".")
    val name = "$packageName.$componentSimpleNames".replace("$", ".")
    val prefix = if (isFullGraph) FULL_GRAPH_PREFIX else ""
    val fileName = "$prefix$name"
    val graphOutput = filer.safeCreate("$fileName.png")
    val dotOutput = filer.safeCreate("$fileName.dot")
    return graphOutput
      .flatMap { graph ->
        dotOutput.map { dot -> OutputFiles(graph, dot) }
      }
  }
}