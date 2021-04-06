package dev.arunkumar.scabbard.di

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import javax.inject.Inject

interface MultiBindingType

class SimpleMultiBindingType @Inject constructor() : MultiBindingType

class ComplexMultiBindingType
@Inject
constructor(val provisionBinding: ProvisionBinding) : MultiBindingType

@Module
abstract class MultiBindingsProvisionModule {
  @Binds
  @IntoSet
  abstract fun SimpleMultiBindingType.simpleMultiBindingType(): MultiBindingType

  @Binds
  @IntoSet
  abstract fun ComplexMultiBindingType.complexMultiBindingType(): MultiBindingType
}
