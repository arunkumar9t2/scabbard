package dev.arunkumar.scabbard.plugin.writer

import javax.tools.FileObject

data class OutputFiles(val graphOutput: FileObject, val dotOutput: FileObject)