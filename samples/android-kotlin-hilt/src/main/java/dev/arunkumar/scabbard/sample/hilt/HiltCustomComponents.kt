package dev.arunkumar.scabbard.sample.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.DefineComponent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@DefineComponent(parent = ApplicationComponent::class)
interface HiltCustomComponent {
  @DefineComponent.Builder
  interface Builder {
    fun build(): HiltCustomComponent
  }
}

private const val NUMBER = "number"

@Module
@InstallIn(HiltCustomComponent::class)
object HiltCustomModule {
  @Provides
  @Named(NUMBER)
  fun providesNumber(): Int = 10

}

@Singleton
class SingletonBinding @Inject constructor()

class CustomBinding @Inject constructor(@param:Named(NUMBER) private val number: Int)

/**
 * Entry point for exposing `HiltCustomComponent`'s bindings
 */
@EntryPoint
@InstallIn(HiltCustomComponent::class)
interface HiltCustomEntryPoint {
  fun customBinding(): CustomBinding
}