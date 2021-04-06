package dev.arunkumar.scabbard.di

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

class MemberInjectionBinding

class UnScopedBinding @Inject constructor()

@Singleton
class SimpleSingleton @Inject constructor()

@Singleton
class ComplexSingleton
@Inject
constructor(
  private val unScopedBinding: UnScopedBinding,
  private val simpleSingleton: SimpleSingleton,
  private val application: Application
)

class ProvisionBinding

@Module
object ProvisionModule {
  @Provides
  @Singleton
  fun providesProvisionBinding() = ProvisionBinding()
}
