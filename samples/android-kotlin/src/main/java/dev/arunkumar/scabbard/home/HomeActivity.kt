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

package dev.arunkumar.scabbard.home

import android.os.Bundle
import androidx.fragment.app.commit
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import dev.arunkumar.scabbard.R
import dev.arunkumar.scabbard.appComponent
import dev.arunkumar.scabbard.di.ComplexSingleton
import dev.arunkumar.scabbard.di.DaggerDependantComponent
import dev.arunkumar.scabbard.di.SimpleSingleton
import dev.arunkumar.scabbard.di.scope.ActivityScope
import dev.arunkumar.scabbard.home.fragment.HomeFragment
import dev.arunkumar.scabbard.home.fragment.ModuleHolder
import javax.inject.Inject

class HomeActivity : DaggerAppCompatActivity() {
  @Inject
  lateinit var activityDep: ActivityDep

  @Inject
  lateinit var applicationSingleton: SimpleSingleton

  @Inject
  lateinit var complexSingleton: ComplexSingleton

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    supportFragmentManager.commit {
      replace(
        R.id.fragmentContainer,
        HomeFragment()
      )
    }

    // Setup simple subcomponent
    application.appComponent.simpleSubcomponentFactory().create()
  }

  @Module
  interface Builder {
    @ActivityScope
    @ContributesAndroidInjector(modules = [ModuleHolder.HomeFragmentBuilder::class])
    fun homeActivity(): HomeActivity
  }
}
