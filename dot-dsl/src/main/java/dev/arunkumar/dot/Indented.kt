package dev.arunkumar.dot

import java.io.PrintWriter

abstract class Indented {

    internal abstract fun write(level: Int, writer: PrintWriter)

    internal fun indent(
        level: Int,
        writer: PrintWriter
    ): PrintWriter {
        writer.print(" ".repeat(level * 2))
        return writer
    }
}