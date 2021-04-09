package dev.arunkumar.scabbard.di

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import javax.inject.Inject

interface MultiBinding

class SimpleMultiBinding @Inject constructor() : MultiBinding

class ComplexMultiBinding
@Inject
constructor(val provisionBinding: ProvisionBinding) : MultiBinding

@Module
abstract class MultiBindingsProvisionModule {
  @Binds
  @IntoSet
  abstract fun SimpleMultiBinding.simpleMultiBindingType(): MultiBinding

  @Binds
  @IntoSet
  abstract fun ComplexMultiBinding.complexMultiBindingType(): MultiBinding
}
