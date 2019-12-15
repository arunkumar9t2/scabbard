package dev.arunkumar.scabbard.intellij.utill

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.Function

fun prepareLineMarkerOpenerForFileName(
  element: PsiElement,
  componentName: String,
  fileName: String
): LineMarkerInfo<PsiElement>? {
  // TODO(arun) Need to optimize this - try to scope it to the module where scabbard is applied
  val scope = GlobalSearchScope.allScope(element.project)
  FilenameIndex.getFilesByName(element.project, fileName, scope)
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