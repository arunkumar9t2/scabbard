package dev.arunkumar.scabbard.plugin.output

import javax.lang.model.element.TypeElement

interface OutputManager {
  fun createOutputFiles(currentComponent: TypeElement): OutputFiles
}