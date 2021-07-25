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
