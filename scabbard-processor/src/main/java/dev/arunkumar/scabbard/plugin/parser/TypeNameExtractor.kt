package dev.arunkumar.scabbard.plugin.parser

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import dagger.Module
import dagger.Provides
import dev.arunkumar.scabbard.plugin.di.ProcessorScope
import dev.arunkumar.scabbard.plugin.options.ScabbardOptions
import javax.inject.Inject
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.*
import javax.lang.model.type.TypeKind.*
import javax.lang.model.util.SimpleTypeVisitor6


/**
 * A extractor to calculate a [TypeMirror]'s or [TypeElement]'s actual name
 */
interface TypeNameExtractor {
  /**
   * @return the string representation of the given [typeMirror]
   */
  fun extractName(typeMirror: TypeMirror): String

  /**
   * @return the string representation of the given [typeElement]
   */
  fun extractName(typeElement: TypeElement): String
}

@Module
object TypeNameExtractorModule {
  @Provides
  @JvmStatic
  @ProcessorScope
  fun typeNameExtractor(
    scabbardOptions: ScabbardOptions,
    qualifiedTypeNameExtractor: QualifiedTypeNameExtractor,
    simpleTypeNameExtractor: SimpleTypeNameExtractor
  ): TypeNameExtractor {
    return if (scabbardOptions.qualifiedNames) {
      qualifiedTypeNameExtractor
    } else {
      simpleTypeNameExtractor
    }
  }
}

/**
 * A [TypeNameExtractor] implementation that returns the fully qualified name of the type.
 *
 * Example for [List] the result would be [java.util.List]
 */
class QualifiedTypeNameExtractor @Inject constructor() : TypeNameExtractor {
  override fun extractName(typeMirror: TypeMirror) = typeMirror.toString()
  override fun extractName(typeElement: TypeElement) = typeElement.toString()
}

/**
 * A [TypeNameExtractor] implementation that returns the simple name of the type
 */
class SimpleTypeNameExtractor @Inject constructor() : TypeNameExtractor {

  override fun extractName(typeMirror: TypeMirror) = typeToSimpleNameString(typeMirror)

  override fun extractName(typeElement: TypeElement) = typeElement.simpleName.toString()

  /**
   * Recursively each type and in the given [typeMirror] and calculates simple name.
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
    type.accept(object : SimpleTypeVisitor6<Void, Void>() {
      override fun visitDeclared(declaredType: DeclaredType, p: Void?): Void? {
        val typeElement = declaredType.asElement() as TypeElement
        rawTypeToString(builder, typeElement, innerClassSeparator);
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
        builder.append(typeVariable.asElement().simpleName);
        return null
      }

      override fun visitError(errorType: ErrorType, p: Void?): Void? {
        builder.append(errorType.toString());
        return null
      }

      override fun defaultAction(typeMirror: TypeMirror, p: Void?): Void? {
        builder.append(TypeName.get(typeMirror).toString())
        return null
      }
    }, null)
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

  fun box(primitiveType: PrimitiveType): TypeName? {
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