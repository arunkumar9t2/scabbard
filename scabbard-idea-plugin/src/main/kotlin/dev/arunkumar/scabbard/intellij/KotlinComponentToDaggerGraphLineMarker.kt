package dev.arunkumar.scabbard.intellij

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import dev.arunkumar.scabbard.intellij.utill.DAGGER_COMPONENT
import dev.arunkumar.scabbard.intellij.utill.DAGGER_SUBCOMPONENT
import dev.arunkumar.scabbard.intellij.utill.prepareLineMarkerOpenerForFileName
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.lexer.KtTokens.*
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile

class KotlinComponentToDaggerGraphLineMarker : LineMarkerProvider {

  override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
    when (element) {
      is LeafPsiElement -> {
        element.getClassOrInterface()?.let { ktClass ->

          if (ktClass.hasDaggerComponentAnnotations()) {
            val componentName = ktClass.name

            (ktClass.containingFile as? KtFile)?.packageFqName?.asString()?.let { packageName ->
              val fileNameToFind = "$packageName.$componentName.png"
              return prepareLineMarkerOpenerForFileName(
                element,
                componentName!!,
                fileNameToFind
              )
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