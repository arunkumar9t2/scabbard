package dev.arunkumar.scabbard.sample.hilt

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Inject

@AndroidEntryPoint
class SimpleService : Service() {

  private val binder = LocalBinder()

  inner class LocalBinder : Binder() {
    fun getService(): SimpleService = this@SimpleService
  }

  override fun onBind(intent: Intent): IBinder = binder

  @Inject
  lateinit var serviceDependency: ServiceDependency

  @ServiceScoped
  class ServiceDependency @Inject constructor()
}
