package dev.arunkumar.scabbard.di

import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

class NamedBinding

@Module
object NamedProvisionModule {
  @Provides
  @Named("named")
  @Singleton
  fun providesNamedBinding() = NamedBinding()
}

@Qualifier
@Retention
annotation class SimpleQualifier

class QualifiedBinding

@Module
object QualifiedProvisionModule {
  @SimpleQualifier
  @Provides
  fun providesQualifiedBinding() = QualifiedBinding()
}
