package dev.arunkumar.scabbard.intellij

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import dev.arunkumar.scabbard.intellij.utill.DAGGER_COMPONENT
import dev.arunkumar.scabbard.intellij.utill.DAGGER_MODULE
import dev.arunkumar.scabbard.intellij.utill.DAGGER_SUBCOMPONENT
import dev.arunkumar.scabbard.intellij.utill.prepareDaggerComponentLineMarkerWithFileName
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.lexer.KtTokens.*
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject

class KotlinComponentToDaggerGraphLineMarker : RelatedItemLineMarkerProvider() {

  private fun KtClassOrObject.hasDaggerComponentAnnotations(): Boolean {
    return findAnnotation(FqName(DAGGER_COMPONENT)) != null
        || findAnnotation(FqName(DAGGER_SUBCOMPONENT)) != null
        || findAnnotation(FqName(DAGGER_MODULE)) != null
  }

  private fun LeafPsiElement.getClassOrInterface(): KtClassOrObject? {
    val isAClassType = (text == CLASS_KEYWORD.value
        || text == OBJECT_KEYWORD.value
        || text == INTERFACE_KEYWORD.value)
    if (elementType is KtKeywordToken && isAClassType) {
      val classOrObjectCandidate = parent
      if (classOrObjectCandidate is KtClassOrObject) {
        return classOrObjectCandidate
      }
    }
    return null
  }

  override fun collectNavigationMarkers(
    element: PsiElement,
    result: MutableCollection<in RelatedItemLineMarkerInfo<PsiElement>>
  ) {
    when (element) {
      is LeafPsiElement -> {
        element.getClassOrInterface()?.let { ktClass ->
          if (ktClass.hasDaggerComponentAnnotations()) {
            val componentName = ktClass.name
            val qualifiedName = ktClass.getKotlinFqName().toString()
            val graphGutterIcon = prepareDaggerComponentLineMarkerWithFileName(
              element = element,
              componentName = componentName!!,
              fileName = qualifiedName
            )
            graphGutterIcon?.let { result.add(graphGutterIcon) }
          }
        }
      }
    }
  }
}