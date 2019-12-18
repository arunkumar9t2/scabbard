package dev.arunkumar.scabbard.plugin.writer

import javax.lang.model.element.TypeElement

interface OutputWriter {
  fun createOutputFiles(currentComponent: TypeElement): OutputFiles
}