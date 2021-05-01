package dev.arunkumar.scabbard.intellij.dagger.console.filters

interface MissingBindingComponentExtractor {
  fun extract(line: String, entireLength: Int): Set<DaggerComponent>
}

class DefaultMissingBindingComponentExtractor : MissingBindingComponentExtractor {
  companion object {
    private val DAGGER_ERROR_FORMAT = "error: \\[Dagger/[a-zA-Z]+]".toRegex()
    private const val EXTENDS = "extends"
    private const val IMPLEMENTS = "implements"
  }

  private var isDaggerLog = false

  override fun extract(line: String, entireLength: Int): Set<DaggerComponent> {
    if (line.contains(DAGGER_ERROR_FORMAT)) {
      isDaggerLog = true
      return emptySet()
    }
    if (isDaggerLog) {
      val lineStart = entireLength - line.length
      // Parse the component simple name from dagger component error line
      if (line.endsWith("{") || line.endsWith(",")) {
        val classNameWithModifiers = when {
          line.contains(EXTENDS) -> line.split(EXTENDS).first()
          line.contains(IMPLEMENTS) -> line.split(IMPLEMENTS).first()
          else -> line.split("{").first()
        }
        // parse class name from string such as "public abstract class AppComponent"
        classNameWithModifiers
          .split(" ")
          .map { it.trim() }
          .lastOrNull { it.isNotBlank() }
          ?.let { componentName ->
            val start = line.indexOf(componentName) + lineStart
            val end = start + componentName.length
            isDaggerLog = false
            return setOf(DaggerComponent(start, end, componentName))
          }
      }
    }
    return emptySet()
  }
}
