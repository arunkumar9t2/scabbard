package dev.arunkumar.scabbard.intellij

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.PsiElement
import dev.arunkumar.scabbard.intellij.utill.DAGGER_CONTRIBUTES_ANDROID_INJECTOR
import dev.arunkumar.scabbard.intellij.utill.prepareContributesAndroidInjectorLineMarker
import org.jetbrains.kotlin.idea.quickfix.createFromUsage.callableBuilder.getReturnTypeReference
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtNamedFunction

class KotlinContributesAndroidInjectorLineMarker : RelatedItemLineMarkerProvider() {

  override fun collectNavigationMarkers(
    element: PsiElement,
    result: MutableCollection<in RelatedItemLineMarkerInfo<PsiElement>>
  ) {
    if (element is KtNamedFunction) {
      // Check if method has @ContributesAndroidInjector
      val crInjector = element.findAnnotation(FqName(DAGGER_CONTRIBUTES_ANDROID_INJECTOR))
      if (crInjector != null) {
        val methodName = element.name
        val packageName = element.containingKtFile.packageFqName.asString()
        // FqName contains the full path to method like `ClassName.method`.
        // We exclude the method name, to process inner classes first
        val qualifiedPath = element.fqName?.asString()?.split(".$methodName")?.first()
        val returnTypeSimpleName = element.getReturnTypeReference()?.text
        if (returnTypeSimpleName != null && qualifiedPath != null && methodName != null) {
          val graphGutterIcon = prepareContributesAndroidInjectorLineMarker(
            contributesAndroidInjectorElement = crInjector,
            packageName = packageName,
            qualifiedPath = qualifiedPath,
            methodName = methodName,
            returnTypeSimpleName = returnTypeSimpleName
          )
          graphGutterIcon?.let { result.add(graphGutterIcon) }
        }
      }
    }
  }
}