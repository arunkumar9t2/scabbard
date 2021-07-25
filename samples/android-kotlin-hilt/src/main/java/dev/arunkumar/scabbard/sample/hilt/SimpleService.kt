/*
 * Copyright 2021 Arunkumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
