package dev.arunkumar.scabbard.plugin.output

import javax.tools.FileObject

data class OutputFiles(val graphOutput: FileObject, val dotOutput: FileObject)