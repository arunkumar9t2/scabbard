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
import com.intellij.psi.impl.source.tree.LeafPsiElement
import dev.arunkumar.scabbard.intellij.dagger.prepareDaggerComponentLineMarkerWithFileName
import dev.arunkumar.scabbard.intellij.dagger.psi.hasAnnotation
import dev.arunkumar.scabbard.intellij.dagger.psi.ktClassOrObject
import dev.arunkumar.scabbard.intellij.dagger.psi.toPsiClass
import org.jetbrains.kotlin.psi.KtClassOrObject

/** Provides gutters for `@DefineComponent` and `@EntryPoint`. */
class KotlinHiltCustomComponentToDaggerGraphLineMarker : RelatedItemLineMarkerProvider() {

  private fun KtClassOrObject.hasCustomHiltComponentAnnotations(): Boolean {
    return hasAnnotation(DAGGER_HILT_DEFINE_COMPONENT) || hasAnnotation(DAGGER_HILT_ENTRY_POINT)
  }

  private fun KtClassOrObject.findGeneratedCustomHiltComponent(): PsiClass? {
    return toPsiClass()?.let { findGeneratedCustomHiltComponent(it) }
  }

  override fun collectNavigationMarkers(
    element: PsiElement,
    result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
  ) {
    when (element) {
      is LeafPsiElement -> {
        element.ktClassOrObject()?.let { ktClass ->
          if (ktClass.hasCustomHiltComponentAnnotations()) {
            val qualifiedName = ktClass.findGeneratedCustomHiltComponent()?.qualifiedName
            qualifiedName?.let {
              val graphLineMarker = prepareDaggerComponentLineMarkerWithFileName(
                element = element,
                componentName = ktClass.name!!,
                componentFqcn = qualifiedName
              )
              graphLineMarker?.let { result.add(graphLineMarker) }
            }
          }
        }
      }
    }
  }
}
