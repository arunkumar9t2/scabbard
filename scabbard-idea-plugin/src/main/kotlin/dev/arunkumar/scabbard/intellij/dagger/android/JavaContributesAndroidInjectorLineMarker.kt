/*
 * Copyright 2021 Arunkumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.arunkumar.scabbard.intellij.dagger.android

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.impl.source.PsiJavaFileImpl
import dev.arunkumar.scabbard.intellij.dagger.DAGGER_CONTRIBUTES_ANDROID_INJECTOR
import dev.arunkumar.scabbard.intellij.dagger.prepareContributesAndroidInjectorLineMarker

class JavaContributesAndroidInjectorLineMarker : RelatedItemLineMarkerProvider() {

  private fun PsiMethod.findAnnotation(annotationName: String): PsiAnnotation? {
    return annotations.firstOrNull { it.hasQualifiedName(annotationName) }
  }

  override fun collectNavigationMarkers(
    element: PsiElement,
    result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
  ) {
    if (element is PsiMethod) {
      // Check if method has @ContributesAndroidInjector
      val crInjector = element.findAnnotation(DAGGER_CONTRIBUTES_ANDROID_INJECTOR)
      if (crInjector != null) {
        val packageName = (element.containingFile as? PsiJavaFileImpl)?.packageName
        val returnTypeSimpleName = (element.returnType as PsiClassReferenceType).className
        val qualifiedPath = element.containingClass?.qualifiedName
        val methodName = element.name
        if (returnTypeSimpleName != null && qualifiedPath != null && packageName != null) {
          val graphLineMarker = prepareContributesAndroidInjectorLineMarker(
            contributesAndroidInjectorElement = crInjector,
            packageName = packageName,
            qualifiedPath = qualifiedPath,
            methodName = methodName,
            returnTypeSimpleName = returnTypeSimpleName
          )
          graphLineMarker?.let { result.add(graphLineMarker) }
        }
      }
    }
  }
}
