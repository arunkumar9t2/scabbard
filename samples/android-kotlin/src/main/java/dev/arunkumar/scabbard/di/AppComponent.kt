/*
 * Copyright 2022 Arunkumar
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

package dev.arunkumar.scabbard.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dev.arunkumar.scabbard.App
import dev.arunkumar.scabbard.home.HomeActivity
import dev.arunkumar.scabbard.home.simple.SimpleSubcomponent
import javax.inject.Singleton

@Singleton
@Component(
  modules = [
    ProvisionModule::class,
    NamedProvisionModule::class,
    DelegateBindingModule::class,
    QualifiedProvisionModule::class,
    MultiBindingsProvisionModule::class,
    AndroidSupportInjectionModule::class,

    // Activities
    HomeActivity.Builder::class
  ],
  dependencies = [DependantComponent::class]
)
interface AppComponent : AndroidInjector<App> {

  fun inject(memberInjectionBinding: MemberInjectionBinding)

  fun simpleSubcomponentFactory(): SimpleSubcomponent.Factory

  fun helloWorld(): HelloWorld

  @Component.Factory
  interface Factory {
    fun create(
      @BindsInstance application: Application,
      dependantComponent: DependantComponent
    ): AppComponent
  }
}
