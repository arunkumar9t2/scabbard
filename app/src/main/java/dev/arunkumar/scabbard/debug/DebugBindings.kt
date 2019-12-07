package dev.arunkumar.scabbard.debug

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApplicationSingleton @Inject constructor()


class UnscopedBinding @Inject constructor()

@Singleton
class ComplexSingleton @Inject constructor(private val unscopedBinding: UnscopedBinding)

class DummyInjectionTarget