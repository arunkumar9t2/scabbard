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

package dev.arukumar.scabbard.anvil

import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import javax.inject.Inject

interface AppScope

interface Binding

class DefaultBinding @Inject constructor() : Binding

@Module
@ContributesTo(AppScope::class)
interface DaggerModule {
  @Binds
  fun DefaultBinding.bind(): Binding
}

@ContributesTo(AppScope::class)
interface ComponentInterface {
  fun binding(): Binding

  fun setMultiBindings(): Set<@JvmSuppressWildcards ContributedMultiBinding>

  fun mapMultiBindings(): Map<String, Listener>
}
