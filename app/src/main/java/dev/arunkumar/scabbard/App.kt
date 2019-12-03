package dev.arunkumar.scabbard

import android.app.Application
import javax.inject.Inject
import javax.inject.Singleton

class App : Application() {
    val appComponent by lazy { DaggerAppComponent.factory().build(this) }
}

fun Application.appComponent() = (this as App).appComponent

@Singleton
class SimpleSingleton @Inject constructor()