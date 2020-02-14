package dev.arunkumar.scabbard.intellij

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiElement
import dev.arunkumar.scabbard.intellij.utill.DAGGER_CONTRIBUTES_ANDROID_INJECTOR
import dev.arunkumar.scabbard.intellij.utill.prepareLineMarkerOpenerForFileName
import org.jetbrains.kotlin.idea.quickfix.createFromUsage.callableBuilder.getReturnTypeReference
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtNamedFunction

class KotlinContributesAndroidInjectorLineMarker : LineMarkerProvider {
  private val innerClassSeparator = '_'

  override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
    if (element is KtNamedFunction) {
      val methodName = element.name
      if (methodName != null) {
        // Check if method has @ContributesAndroidInjector
        val cInjector = element.findAnnotation(FqName(DAGGER_CONTRIBUTES_ANDROID_INJECTOR))
        if (cInjector != null) {
          val packageName = element.containingKtFile.packageFqName.asString()
          // FqName contains the full path to method like `ClassName.method`.
          // We exclude the method name, to process inner classes first
          val qualifiedPath = element.fqName?.asString()?.split(".$methodName")?.first()
          if (qualifiedPath != null) {
            // Replace inner class paths with "_" and capitalize the method name to get the path
            val generatedImagePath = "$packageName." + qualifiedPath
              .substring(packageName.length + 1)
              .replace('.', innerClassSeparator) + "_${methodName.capitalize()}"

            // We could infer the generated subcomponent name by simply getting the return type and
            // suffixing "SubComponent"
            val subcomponentName = element.getReturnTypeReference()?.text + "Subcomponent"
            val generatedImageName = "$generatedImagePath.$subcomponentName"
            return prepareLineMarkerOpenerForFileName(
              element = cInjector,
              componentName = subcomponentName,
              fileName = "$generatedImageName.png"
            )
          }
        }
      }
      return null
    } else {
      return null
    }
  }
}