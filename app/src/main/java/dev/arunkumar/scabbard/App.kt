package dev.arunkumar.scabbard

import android.app.Application
import javax.inject.Inject

class App : Application() {
    val appComponent by lazy { DaggerAppComponent.factory().build(this) }

    @Inject
    lateinit var aSingleton: ASingleton

    override fun onCreate() {
        appComponent.inject(this)
        super.onCreate()
    }
}

fun Application.appComponent() = (this as App).appComponent