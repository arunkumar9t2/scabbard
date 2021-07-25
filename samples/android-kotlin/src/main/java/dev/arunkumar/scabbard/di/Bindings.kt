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

package dev.arunkumar.scabbard.di

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

class MemberInjectionBinding

class UnScopedBinding @Inject constructor()

@Singleton
class SimpleSingleton @Inject constructor()

@Singleton
class ComplexSingleton
@Inject
constructor(
  private val unScopedBinding: UnScopedBinding,
  private val simpleSingleton: SimpleSingleton,
  private val application: Application
)

class ProvisionBinding

@Module
object ProvisionModule {
  @Provides
  @Singleton
  fun providesProvisionBinding() = ProvisionBinding()
}
