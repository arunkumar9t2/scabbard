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
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.GlobalSearchScope.allScope
import com.intellij.psi.search.GlobalSearchScope.moduleWithDependenciesAndLibrariesScope
import com.intellij.util.Function
import dev.arunkumar.scabbard.intellij.utill.DAGGER_COMPONENT
import dev.arunkumar.scabbard.intellij.utill.DAGGER_SUBCOMPONENT
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.idea.util.module
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.lexer.KtTokens.*
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import java.io.File.separator

class KotlinComponentToDaggerGraphLineMarker : LineMarkerProvider {

  override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
    val project = element.project
    when (element) {
      is LeafPsiElement -> {
        element.getClassOrInterface()?.let { ktClass ->
          if (hasDaggerComponentAnnotations(ktClass)) {
            val componentName = ktClass.name

            (ktClass.containingFile as? KtFile)?.packageFqName?.asString()?.let { packageName ->
              val fileNameToFind = "$componentName.png"
              val directoryShouldEndWith =
                packageName.replace(".", separator) + separator + ktClass.name

              val scope: GlobalSearchScope = when {
                element.module != null -> moduleWithDependenciesAndLibrariesScope(element.module!!)
                else -> allScope(project)
              }

              FilenameIndex.getFilesByName(project, fileNameToFind, scope)
                .filter { it.containingDirectory.toString().endsWith(directoryShouldEndWith) }
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

  private fun hasDaggerComponentAnnotations(ktClass: KtClassOrObject): Boolean {
    return (ktClass.findAnnotation(FqName(DAGGER_COMPONENT)) != null
        || ktClass.findAnnotation(FqName(DAGGER_SUBCOMPONENT)) != null)
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