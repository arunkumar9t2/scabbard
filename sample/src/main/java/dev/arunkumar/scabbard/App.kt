package dev.arunkumar.scabbard

import dagger.android.support.DaggerApplication
import dev.arunkumar.scabbard.debug.*
import dev.arunkumar.scabbard.di.DaggerAppComponent
import javax.inject.Inject
import javax.inject.Named

class App : DaggerApplication() {
  val appComponent by lazy { DaggerAppComponent.factory().build(this) }

  @Inject
  lateinit var complexSingleton: ComplexSingleton
  @Inject
  lateinit var provisionBinding: ProvisionBinding
  @Inject
  @JvmSuppressWildcards
  lateinit var multiBindingTypes: Set<MultiBindingType>
  @Inject
  lateinit var delegateBinding: DelegateBinding
  @Inject
  @field:Named("named")
  lateinit var namedBinding: NamedBinding

  override fun applicationInjector() = appComponent

  override fun onCreate() {
    appComponent.inject(this)
    super.onCreate()
  }
}