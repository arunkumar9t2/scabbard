package dev.arunkumar.scabbard.intellij

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope.allScope
import com.intellij.util.Function
import dev.arunkumar.scabbard.intellij.utill.DAGGER_COMPONENT
import dev.arunkumar.scabbard.intellij.utill.DAGGER_SUBCOMPONENT
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.lexer.KtTokens.*
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile

class KotlinComponentToDaggerGraphLineMarker : LineMarkerProvider {

  override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
    val project = element.project
    when (element) {
      is LeafPsiElement -> {
        element.getClassOrInterface()?.let { ktClass ->

          if (ktClass.hasDaggerComponentAnnotations()) {
            val componentName = ktClass.name

            (ktClass.containingFile as? KtFile)?.packageFqName?.asString()?.let { packageName ->
              val fileNameToFind = "$packageName.$componentName.png"
              /*val scope: GlobalSearchScope = when {
                element.module != null -> moduleWithDependenciesAndLibrariesScope(element.module!!)
                else -> allScope(project)
              }*/

              // TODO(arun) Need to optimize this - try to scope it to the module where scabbard is applied
              val scope = allScope(element.project)

              FilenameIndex.getFilesByName(project, fileNameToFind, scope)
                .takeIf { it.isNotEmpty() }
                ?.first()
                ?.let { graphFile ->
                  return LineMarkerInfo<PsiElement>(
                    element,
                    element.textRange,
                    IconLoader.getIcon("/icons/scabbard-icon.png"),
                    Function { "Open ${componentName}'s dependency graph" },
                    GutterIconNavigationHandler<PsiElement?> { _, _ ->
                      FileEditorManager.getInstance(project).openFile(
                        graphFile.virtualFile,
                        true,
                        true
                      )
                    },
                    GutterIconRenderer.Alignment.CENTER
                  )
                }
            }
          }
        }
        return null
      }
      else -> return null
    }
  }

  private fun KtClassOrObject.hasDaggerComponentAnnotations(): Boolean {
    return findAnnotation(FqName(DAGGER_COMPONENT)) != null
        || findAnnotation(FqName(DAGGER_SUBCOMPONENT)) != null
  }

  private fun LeafPsiElement.getClassOrInterface(): KtClassOrObject? {
    if (elementType is KtKeywordToken && (
          text == CLASS_KEYWORD.value
              || text == OBJECT_KEYWORD.value // TODO(arun) do we need object?
              || text == INTERFACE_KEYWORD.value)
    ) {
      val maybeKtClassOrObject = parent
      if (maybeKtClassOrObject is KtClassOrObject) {
        return maybeKtClassOrObject
      }
    }
    return null
  }
}