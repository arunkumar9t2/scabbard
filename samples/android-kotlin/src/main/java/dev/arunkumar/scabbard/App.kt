package dev.arunkumar.scabbard

import android.app.Application
import dagger.android.support.DaggerApplication
import dev.arunkumar.scabbard.di.*
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
  lateinit var multiBindings: Set<MultiBinding>

  @Inject
  lateinit var delegateBinding: DelegateBinding

  @Inject
  @Named("named")
  lateinit var namedBinding: NamedBinding

  @Inject
  @SimpleQualifier
  lateinit var qualifiedBinding: QualifiedBinding

  override fun applicationInjector() = appComponent

  override fun onCreate() {
    appComponent.inject(this)
    super.onCreate()
  }
}

val Application.appComponent get() = (this as App).appComponent
