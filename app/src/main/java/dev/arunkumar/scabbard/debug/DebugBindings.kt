package dev.arunkumar.scabbard.debug

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

// Targets
class DummyInjectionTarget