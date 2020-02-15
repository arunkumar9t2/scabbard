package dev.arunkumar.scabbard.intellij.utill

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import com.intellij.util.Function

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
    // we could get the correct package name by querying IntelliJ class index.
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
          "$qualifiedName.png"
        )
      }
  }
  return null
}

fun prepareLineMarkerOpenerForFileName(
  element: PsiElement,
  componentName: String,
  fileName: String
): LineMarkerInfo<PsiElement>? {
  // TODO(arun) Need to optimize this - try to scope it to the module where scabbard is applied
  val scope = GlobalSearchScope.allScope(element.project)
  FilenameIndex.getFilesByName(element.project, fileName.replace("$", "."), scope)
    .takeIf { it.isNotEmpty() }
    ?.first()
    ?.let { graphFile ->
      return LineMarkerInfo<PsiElement>(
        element,
        element.textRange,
        IconLoader.getIcon("/icons/scabbard-icon.png"),
        Function { "Open ${componentName}'s dependency graph" },
        GutterIconNavigationHandler<PsiElement?> { _, _ ->
          FileEditorManager.getInstance(element.project).openFile(
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