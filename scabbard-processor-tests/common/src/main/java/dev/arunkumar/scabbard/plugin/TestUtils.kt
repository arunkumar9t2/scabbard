package dev.arunkumar.scabbard.plugin

import dev.arunkumar.scabbard.plugin.output.DefaultOutputManager
import guru.nidi.graphviz.model.MutableGraph
import guru.nidi.graphviz.parse.Parser
import java.io.File
import java.nio.file.Paths

fun Class<*>.parsedGraph(): MutableGraph = Parser().read(generatedDotFile().readText())

fun Class<*>.generatedDotFile(fileNamePrefix: String = ""): File {
  //TODO(arun) The proper way would be to use Resources.getResource() but that does not seem to work
  val projectDir = System.getProperty("user.dir")
  val sep = File.separatorChar.toString()
  val name = name.replace("$", ".")
  val pathToGenFile = DefaultOutputManager.SCABBARD_PACKAGE
  val absoluteGenPath = "$projectDir${sep}build/tmp/kapt3/classes/test${sep}$pathToGenFile${sep}"
  return Paths.get(absoluteGenPath).resolve("$fileNamePrefix$name.dot").toFile()
}

fun Class<*>.generatedFullDotFile(): File {
  return generatedDotFile(fileNamePrefix = DefaultOutputManager.SCABBARD_PACKAGE)
}