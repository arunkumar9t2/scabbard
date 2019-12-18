package dev.arunkumar.scabbard.plugin.writer

import javax.annotation.processing.Filer

interface FilerOutputWriter : OutputWriter {
  val filer: Filer
}