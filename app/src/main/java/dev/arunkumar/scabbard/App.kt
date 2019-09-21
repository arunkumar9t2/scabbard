package dev.arunkumar.scabbard

import dagger.android.support.DaggerApplication

class App : DaggerApplication() {

    private val appComponent by lazy { DaggerAppComponent.factory().build(this) }

    override fun applicationInjector() = appComponent
}