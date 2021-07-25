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

package dev.arunkumar.scabbard.plugin.parser

import com.google.auto.common.AnnotationMirrors
import com.google.common.base.Joiner
import com.google.common.collect.ImmutableMap
import com.squareup.javapoet.CodeBlock
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.SimpleAnnotationValueVisitor8

private fun AnnotationValue.extractName(): String {
  val visitor = object : SimpleAnnotationValueVisitor8<String, Void>() {

    override fun defaultAction(
      value: Any?,
      ignore: Void?
    ): String = value.toString()

    override fun visitString(
      value: String?,
      ignore: Void?
    ): String = CodeBlock.of("\$S", value).toString()

    override fun visitAnnotation(
      value: AnnotationMirror,
      ignore: Void?
    ): String = value.extractName()

    override fun visitArray(
      value: List<AnnotationValue>,
      ignore: Void?
    ): String = value
      .map { it.extractName() }
      .toList()
      .joinToString(separator = ", ", prefix = "{", postfix = "}")
  }
  return accept<String, Void>(visitor, null)
}

/**
 * Parse an [AnnotationMirror] and return the string representation.
 *
 * @param typeParser Uses this function to compute the annotation name. The default the implementation
 * simple calls [toString]
 */
fun AnnotationMirror.extractName(typeParser: (TypeMirror) -> String = { it.toString() }): String {
  val annotationName = typeParser(annotationType.asElement().asType())
  val stringBuilder = StringBuilder("@").append(annotationName)
  val elementValues = AnnotationMirrors.getAnnotationValuesWithDefaults(this)
  // TODO(arun) Kotlinize this
  if (!elementValues.isEmpty()) {
    val namedValuesBuilder = ImmutableMap.builder<String?, String?>()
    elementValues.forEach { (key: ExecutableElement, value: AnnotationValue?) ->
      namedValuesBuilder.put(
        key.simpleName.toString(),
        value.extractName()
      )
    }
    val namedValues = namedValuesBuilder.build()
    stringBuilder.append('(')
    if (namedValues.size == 1 && namedValues.containsKey("value")) { // Omit "value ="
      stringBuilder.append(namedValues["value"])
    } else {
      stringBuilder.append(
        Joiner.on(", ")
          .withKeyValueSeparator("=")
          .join(namedValues)
      )
    }
    stringBuilder.append(')')
  }
  return stringBuilder.toString()
}
