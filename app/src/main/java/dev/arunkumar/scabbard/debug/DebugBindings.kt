package dev.arunkumar.scabbard.debug

import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApplicationSingleton
@Inject
constructor(private val unScopedBinding: UnScopedBinding)

class UnScopedBinding @Inject constructor()

@Singleton
class ComplexSingleton
@Inject
constructor(
    private val unScopedBinding: UnScopedBinding,
    private val applicationSingleton: ApplicationSingleton
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