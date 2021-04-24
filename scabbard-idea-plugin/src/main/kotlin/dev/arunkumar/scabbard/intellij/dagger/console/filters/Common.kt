package dev.arunkumar.scabbard.intellij.dagger.console.filters

interface ConsoleResult {
  val highlightStartOffset: Int
  val highlightEndOffset: Int
}

data class DaggerComponent(
  override val highlightStartOffset: Int,
  override val highlightEndOffset: Int,
  val name: String
) : ConsoleResult
