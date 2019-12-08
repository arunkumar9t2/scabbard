package dev.arunkumar.scabbard.debug

import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SimpleSingleton
@Inject
constructor()

class UnScopedBinding @Inject constructor()

@Singleton
class ComplexSingleton
@Inject
constructor(
    private val unScopedBinding: UnScopedBinding,
    private val applicationSingleton: SimpleSingleton
)

// Empty Entry Point
class DummyInjectionTarget

class ProvisionBinding

@Module
object ProvisionModule {
    @Provides
    @Singleton
    @JvmStatic
    fun providesProvisionBinding() = ProvisionBinding()
}