/*
 * Copyright 2022 Arunkumar
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

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import dagger.Module
import dagger.Provides
import dagger.model.ComponentPath
import dagger.model.Key
import dev.arunkumar.scabbard.plugin.di.ProcessorScope
import dev.arunkumar.scabbard.plugin.options.ScabbardOptions
import javax.inject.Inject
import javax.lang.model.element.*
import javax.lang.model.type.*
import javax.lang.model.type.TypeKind.*
import javax.lang.model.util.SimpleTypeVisitor6

interface TypeNameExtractor {

  fun extractName(typeMirror: TypeMirror): String

  fun extractName(annotationMirror: AnnotationMirror): String

  /**
   * @return the component hierarchy in string representation. For
   *     example: "AppComponent → SubComponent"
   */
  fun extractName(componentPath: ComponentPath): String

  /** @return the multibinding contribution info as string */
  fun extractName(identifier: Key.MultibindingContributionIdentifier): String
}

@Module
object TypeNameExtractorModule {
  @Provides
  @ProcessorScope
  fun typeNameExtractor(
    scabbardOptions: ScabbardOptions,
    qualifiedTypeNameExtractor: QualifiedTypeNameExtractor,
    simpleTypeNameExtractor: SimpleTypeNameExtractor
  ): TypeNameExtractor {
    return when {
      scabbardOptions.qualifiedNames -> qualifiedTypeNameExtractor
      else -> simpleTypeNameExtractor
    }
  }
}

/**
 * A [TypeNameExtractor] implementation that returns the fully qualified
 * name of the type. For other types, the implementation simply calls
 * [toString]
 *
 * Example for [List] the result would be [java.util.List]
 */
@Suppress("DEPRECATION")
class QualifiedTypeNameExtractor @Inject constructor() : TypeNameExtractor {
  override fun extractName(typeMirror: TypeMirror) = typeMirror.toString()
  override fun extractName(annotationMirror: AnnotationMirror) = annotationMirror.toString()
  override fun extractName(componentPath: ComponentPath): String = componentPath.toString()
  override fun extractName(identifier: Key.MultibindingContributionIdentifier): String {
    return identifier.let { it.module() + "." + it.bindingElement() + "()" }
  }
}

/**
 * A [TypeNameExtractor] implementation that returns the simple name of
 * the type
 */
@Suppress("DEPRECATION")
class SimpleTypeNameExtractor @Inject constructor() : TypeNameExtractor {

  override fun extractName(typeMirror: TypeMirror) = typeToSimpleNameString(typeMirror)

  override fun extractName(annotationMirror: AnnotationMirror): String {
    return annotationMirror.extractName(typeParser = { type -> extractName(type) })
  }

  override fun extractName(componentPath: ComponentPath) = componentPath
    .components()
    .joinToString(separator = " → ") { extractName(it.asType()) }

  override fun extractName(identifier: Key.MultibindingContributionIdentifier): String {
    val module = identifier.module()
      .split(".")
      .last() // The simple name of the module
      // dagger.android specific optimization (The name is usually Module_MethodName$packageSuffix)
      .split("$")
      .first()
    val bindingElement = identifier.bindingElement()
    return "$module.$bindingElement()"
  }

  /**
   * Recursively traverses each type in the given [typeMirror] and
   * calculates simple name.
   *
   * Based on
   * [https://github.com/square/dagger/blob/master/compiler/src/main/java/dagger/internal/codegen/Util.java#L123]
   */
  private fun typeToSimpleNameString(typeMirror: TypeMirror): String {
    return StringBuilder().let { builder ->
      typeToString(typeMirror, builder, '.')
      builder.toString()
    }
  }

  private fun typeToString(type: TypeMirror, builder: StringBuilder, innerClassSeparator: Char) {
    type.accept(
      object : SimpleTypeVisitor6<Void, Void>() {
        override fun visitDeclared(declaredType: DeclaredType, p: Void?): Void? {
          val typeElement = declaredType.asElement() as TypeElement
          rawTypeToString(builder, typeElement, innerClassSeparator)
          val typeArguments = declaredType.typeArguments
          if (typeArguments.isNotEmpty()) {
            builder.append("<")
            for (i in typeArguments.indices) {
              if (i != 0) {
                builder.append(", ")
              }
              typeToString(typeArguments[i]!!, builder, innerClassSeparator)
            }
            builder.append(">")
          }
          return null
        }

        override fun visitPrimitive(primitiveType: PrimitiveType, p: Void?): Void? {
          builder.append(box((type as PrimitiveType)))
          return null
        }

        override fun visitArray(arrayType: ArrayType, p: Void?): Void? {
          val componentType = arrayType.componentType
          if (componentType is PrimitiveType) {
            builder.append(componentType.toString()) // Don't box, since this is an array.
          } else {
            typeToString(arrayType.componentType, builder, innerClassSeparator)
          }
          builder.append("[]")
          return null
        }

        override fun visitTypeVariable(typeVariable: TypeVariable, p: Void?): Void? {
          builder.append(typeVariable.asElement().simpleName)
          return null
        }

        override fun visitError(errorType: ErrorType, p: Void?): Void? {
          builder.append(errorType.toString())
          return null
        }

        override fun defaultAction(typeMirror: TypeMirror, p: Void?): Void? {
          builder.append(TypeName.get(typeMirror).toString())
          return null
        }
      },
      null
    )
  }

  private fun rawTypeToString(
    result: StringBuilder,
    type: TypeElement,
    innerClassSeparator: Char
  ) {
    val packageName = getPackage(type).qualifiedName.toString()
    val qualifiedName = type.qualifiedName.toString()
    result.apply {
      if (packageName.isEmpty()) {
        append(qualifiedName.replace('.', innerClassSeparator))
      } else {
        // Interested only in simple names
        // append(packageName)
        // append('.')
        append(
          qualifiedName
            .substring(packageName.length + 1)
            .replace('.', innerClassSeparator)
            // Heurestics: There is a high change some classes esp dagger.android ones have long
            // nested names. For now only consider maximum of 2 levels for reduced width.
            .split(innerClassSeparator)
            .takeLast(2)
            .let { classNames ->
              // Heurestics: For Hilt generated components, the format is usually <app_name>_HiltComponents.<component_nam>
              // In this case, we can filter out the first part and consider <component_name> alone.
              // TODO(arun) Move the constant to common module and share with idea plugin
              if (classNames.firstOrNull()?.endsWith("HiltComponents") == true) {
                listOf(classNames.last())
              } else {
                classNames
              }
            }.joinToString(separator = innerClassSeparator.toString())
        )
      }
    }
  }

  private fun getPackage(type: Element): PackageElement {
    var element: Element = type
    while (element.kind !== ElementKind.PACKAGE) {
      element = element.enclosingElement
    }
    return element as PackageElement
  }

  private fun box(primitiveType: PrimitiveType): TypeName? {
    return when (primitiveType.kind) {
      BYTE -> ClassName.get(Byte::class.java)
      SHORT -> ClassName.get(Short::class.java)
      INT -> ClassName.get(Int::class.java)
      LONG -> ClassName.get(Long::class.java)
      FLOAT -> ClassName.get(Float::class.java)
      DOUBLE -> ClassName.get(Double::class.java)
      BOOLEAN -> ClassName.get(Boolean::class.java)
      CHAR -> ClassName.get(Char::class.java)
      VOID -> ClassName.get(Void::class.java)
      else -> throw AssertionError()
    }
  }
}
