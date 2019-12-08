package dev.arunkumar.scabbard

import android.app.Application
import dev.arunkumar.scabbard.debug.ComplexSingleton
import dev.arunkumar.scabbard.debug.ProvisionBinding
import dev.arunkumar.scabbard.di.DaggerAppComponent
import javax.inject.Inject

class App : Application() {
    val appComponent by lazy { DaggerAppComponent.factory().build(this) }

    @Inject
    lateinit var complexSingleton: ComplexSingleton
    @Inject
    lateinit var provisionBinding: ProvisionBinding

    override fun onCreate() {
        appComponent.inject(this)
        super.onCreate()
    }
}

fun Application.appComponent() = (this as App).appComponent