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

package dev.arunkumar.scabbard.home.fragment

import dagger.android.support.DaggerFragment
import dev.arunkumar.scabbard.di.SimpleSingleton
import dev.arunkumar.scabbard.di.UnScopedBinding
import dev.arunkumar.scabbard.home.ActivityDep
import javax.inject.Inject

class HomeFragment : DaggerFragment() {
  @Inject
  lateinit var activityDep: ActivityDep

  @Inject
  lateinit var fragmentDep: FragmentDep

  @Inject
  lateinit var singleton: SimpleSingleton

  @Inject
  lateinit var unScopedBinding: UnScopedBinding
}

class FragmentDep @Inject constructor()
