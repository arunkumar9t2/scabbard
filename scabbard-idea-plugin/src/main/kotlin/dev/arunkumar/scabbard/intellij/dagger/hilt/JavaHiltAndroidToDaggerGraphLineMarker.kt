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

package dev.arunkumar.scabbard.intellij.dagger.hilt

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import dev.arunkumar.scabbard.intellij.dagger.prepareDaggerComponentLineMarkerWithFileName
import dev.arunkumar.scabbard.intellij.dagger.psi.isDaggerAnnotationIdentifier
import dev.arunkumar.scabbard.intellij.dagger.psi.isSubClassOf
import org.jetbrains.kotlin.j2k.getContainingClass

class JavaHiltAndroidToDaggerGraphLineMarker : RelatedItemLineMarkerProvider() {

  private val hiltAnnotations = listOf(
    DAGGER_HILT_ANDROID_ENTRY_POINT,
    DAGGER_HILT_ANDROID_APP
  )

  private fun PsiClass.findHiltGeneratedComponent() = findGeneratedStandardHiltComponent(
    componentClass = this,
    hasAnnotation = { qualifiedName -> hasAnnotation(qualifiedName) },
    isSubClassOf = { qualifiedClassName -> isSubClassOf(qualifiedClassName) }
  )

  override fun collectNavigationMarkers(
    element: PsiElement,
    result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
  ) {
    if (element.isDaggerAnnotationIdentifier(hiltAnnotations)) {
      val psiClass = element.getContainingClass()
      val qualifiedName = psiClass?.findHiltGeneratedComponent()?.qualifiedName
      qualifiedName?.let {
        val graphLineMarker = prepareDaggerComponentLineMarkerWithFileName(
          element = element,
          componentName = psiClass.name!!,
          componentFqcn = qualifiedName
        )
        graphLineMarker?.let { result.add(graphLineMarker) }
      }
    }
  }
}
