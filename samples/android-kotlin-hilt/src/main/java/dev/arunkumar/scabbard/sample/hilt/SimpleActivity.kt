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

package dev.arunkumar.scabbard.sample.hilt

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@AndroidEntryPoint
class SimpleActivity : AppCompatActivity() {

  @Inject
  lateinit var activityDependency: ActivityDependency

  @Inject
  lateinit var activityRetainedDependency: ActivityRetainedDependency

  @Inject
  lateinit var provisionBinding: ProvisionBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  class MainActivityPresenter @Inject constructor()

  @ActivityScoped
  class ActivityDependency
  @Inject
  constructor(
    private val activity: Activity,
    private val mainActivityPresenter: MainActivityPresenter
  )

  @ActivityRetainedScoped
  class ActivityRetainedDependency @Inject constructor()

  class ProvisionBinding

  @Module
  @InstallIn(ActivityComponent::class)
  object ActivityProvisionModule {
    @ActivityScoped
    @Provides
    fun provisionBinding() = ProvisionBinding()
  }
}
