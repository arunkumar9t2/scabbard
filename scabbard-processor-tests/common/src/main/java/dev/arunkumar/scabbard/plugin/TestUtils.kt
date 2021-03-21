package dev.arunkumar.scabbard.plugin

import dev.arunkumar.scabbard.plugin.output.DefaultOutputManager
import dev.arunkumar.scabbard.plugin.output.DefaultOutputManager.Companion.FULL_GRAPH_PREFIX
import dev.arunkumar.scabbard.plugin.output.DefaultOutputManager.Companion.TREE_GRAPH_PREFIX
import dev.arunkumar.scabbard.plugin.output.OutputManager.Format.DOT
import dev.arunkumar.scabbard.plugin.output.OutputManager.Format.SVG
import guru.nidi.graphviz.model.MutableGraph
import guru.nidi.graphviz.parse.Parser
import java.io.File
import java.nio.file.Paths

const val TEST_SOURCE_SET = "test"

fun Class<*>.parsedGraph(sourceSet: String = TEST_SOURCE_SET): MutableGraph =
  Parser().read(generatedDotFile(sourceSet = sourceSet).readText())

inline fun <reified T> generatedGraph(sourceSet: String = TEST_SOURCE_SET) = T::class.java.parsedGraph(sourceSet)

fun Class<*>.generatedFile(fileNamePrefix: String, extension: String, sourceSet: String = TEST_SOURCE_SET): File {
  // TODO(arun) The proper way would be to use Resources.getResource() but that does not seem to work
  val projectDir = System.getProperty("user.dir")
  val sep = File.separatorChar.toString()
  val name = name.replace("$", ".")
  val pathToGenFile = DefaultOutputManager.SCABBARD_PACKAGE
  val absoluteGenPath = "$projectDir${sep}build/tmp/kapt3/classes/$sourceSet${sep}$pathToGenFile$sep"
  return Paths.get(absoluteGenPath).resolve("$fileNamePrefix$name.$extension").toFile()
}

fun Class<*>.generatedDotFile(fileNamePrefix: String = "", sourceSet: String = TEST_SOURCE_SET): File {
  return generatedFile(fileNamePrefix, DOT.extension, sourceSet)
}

fun Class<*>.generatedSvgFile(fileNamePrefix: String = "", sourceSet: String = TEST_SOURCE_SET): File {
  return generatedFile(fileNamePrefix, SVG.extension, sourceSet)
}

fun Class<*>.generatedFullDotFile(sourceSet: String = TEST_SOURCE_SET): File {
  return generatedDotFile(fileNamePrefix = FULL_GRAPH_PREFIX, sourceSet = sourceSet)
}

inline fun <reified T> generatedComponentTreeDotFile(sourceSet: String = TEST_SOURCE_SET): File {
  return T::class.java.generatedComponentTreeDotFile(sourceSet)
}

fun Class<*>.generatedComponentTreeDotFile(sourceSet: String = TEST_SOURCE_SET): File {
  return generatedDotFile(fileNamePrefix = TREE_GRAPH_PREFIX, sourceSet = sourceSet)
}

inline fun <reified T> generatedDot(fileNamePrefix: String = "", sourceSet: String = TEST_SOURCE_SET): String {
  return T::class.java.generatedDotFile(fileNamePrefix, sourceSet).readText()
}
