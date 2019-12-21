package dev.arunkumar.scabbard.plugin

import java.io.File
import java.nio.file.Paths

fun Class<*>.generatedDotFile(): File {
  //TODO(arun) The proper way would be to use Resources.getResource() but that does not seem to work
  val projectDir = System.getProperty("user.dir")
  val sep = File.separatorChar.toString()
  val name = name.replace("$", ".")
  val pathToGenFile = name.replace(".", sep)
  val absoluteGenPath = "$projectDir${sep}build/tmp/kapt3/classes/test${sep}$pathToGenFile${sep}"
  return Paths.get(absoluteGenPath).resolve("$name.dot").toFile()
}