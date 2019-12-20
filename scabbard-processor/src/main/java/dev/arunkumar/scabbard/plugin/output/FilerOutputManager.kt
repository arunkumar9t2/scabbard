package dev.arunkumar.scabbard.plugin.output

import javax.annotation.processing.Filer

interface FilerOutputManager :
  OutputManager {
  val filer: Filer
}