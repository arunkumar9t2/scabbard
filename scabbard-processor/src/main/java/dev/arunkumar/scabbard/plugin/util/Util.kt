package dev.arunkumar.scabbard.plugin.util

inline fun tryCatchLogging(crossinline block: () -> Unit) {
  try {
    block()
  } catch (e: Exception) {
    e.printStackTrace()
    throw RuntimeException("Scabbard processor failed")
  }
}