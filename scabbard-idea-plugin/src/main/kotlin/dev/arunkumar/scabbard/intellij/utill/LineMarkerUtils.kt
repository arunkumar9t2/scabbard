package dev.arunkumar.scabbard.intellij.utill

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiElement
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import com.intellij.util.Function

private const val FULL_GRAPH_FILE_NAME =
  "full_%s" // TODO(arun) create a common package to share this between intellij and processor

private val GENERATED_FILE_FORMATS = listOf(
  "%s.png",
  "$FULL_GRAPH_FILE_NAME.png",
  "%s.svg",
  "$FULL_GRAPH_FILE_NAME.svg"
)

/**
 * Adds a gutter icon to open the dependency graph for the Subcomponent that `@ContributesAndroidInjector`
 * would be generating.
 *
 * All parameters of this function are related to the method that has the `@ContributesAndroidInjector`
 * annotation.
 *
 * @param contributesAndroidInjectorElement PsiElement representing the `@ContributesAndroidInjector`
 * @param packageName The package name of the containing file
 * @param qualifiedPath The full path to class that contains the method which the annotation.
 * @param methodName The name of the method that has the annotation
 * @param returnTypeSimpleName The simple name of the method's return type.
 */
fun prepareContributesAndroidInjectorLineMarker(
  contributesAndroidInjectorElement: PsiElement,
  packageName: String,
  qualifiedPath: String,
  methodName: String,
  returnTypeSimpleName: String
): LineMarkerInfo<PsiElement>? {
  val innerClassSep = '_'

  // Replace inner class paths with "_" and capitalize the method name to get the path
  val generatedImagePath = "$packageName." + qualifiedPath
    .substring(packageName.length + 1)
    .replace('.', innerClassSep) + "$innerClassSep${methodName.capitalize()}"

  // We could infer the generated subcomponent name by simply getting the return type and
  // suffixing "SubComponent"
  val subcomponentName = returnTypeSimpleName + "Subcomponent"
  val generatedImageName = "$generatedImagePath.$subcomponentName"
  val lineMarker = prepareLineMarkerOpenerForFileName(
    element = contributesAndroidInjectorElement,
    componentName = subcomponentName,
    fileName = "$generatedImageName.png"
  )
  if (lineMarker != null) {
    return lineMarker
  } else {
    // If Android Project uses packageName suffix then generated subcomponent will have suffix in
    // its package name which invalidates all the assumptions we made above. To overcome this,
    // we could get the correct package name by querying IntelliJ class index with the return type.
    val project = contributesAndroidInjectorElement.project
    PsiShortNamesCache.getInstance(project)
      .getClassesByName(
        subcomponentName,
        GlobalSearchScope.projectScope(project)
      ).firstOrNull()
      ?.let { subcomponentClass ->
        val qualifiedName = subcomponentClass.qualifiedName
        return prepareLineMarkerOpenerForFileName(
          contributesAndroidInjectorElement,
          subcomponentName,
          "$qualifiedName"
        )
      }
  }
  return null
}

/**
 * Format and sanitize raw file name received from PSI processors.
 *
 * Currently
 * * replaces inner class markers `$` with `.`
 */
private fun String.sanitize(format: String): String {
  val sanitized = replace("$", ".")
  return String.format(format, sanitized)
}

fun prepareLineMarkerOpenerForFileName(
  element: PsiElement,
  componentName: String,
  fileName: String
): LineMarkerInfo<PsiElement>? {
  // TODO(arun) Need to optimize this - try to scope it to the module where scabbard is applied
  val scope = GlobalSearchScope.allScope(element.project)
  GENERATED_FILE_FORMATS
    .flatMap { fileNameFormat ->
      FilenameIndex.getFilesByName(
        element.project,
        fileName.sanitize(fileNameFormat),
        scope
      ).toList()
    }.firstOrNull() // TODO(arun) Migrate to showing popup for multiple files
    ?.let { graphFile ->
      return LineMarkerInfo<PsiElement>(
        element,
        element.textRange,
        SCABBARD_ICON,
        Function { "Open ${componentName}'s dependency graph" },
        GutterIconNavigationHandler<PsiElement?> { _, _ ->
          FileEditorManager.getInstance(element.project)
            .openFile(
              graphFile.virtualFile,
              true,
              true
            )
        },
        GutterIconRenderer.Alignment.CENTER
      )
    }
  return null
}