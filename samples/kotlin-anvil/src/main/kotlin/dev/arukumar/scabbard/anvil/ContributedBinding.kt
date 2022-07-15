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

import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import dagger.MapKey
import javax.inject.Inject

//region ContributedBinding
interface ContributedBinding

@ContributesBinding(AppScope::class)
class DefaultContributedBinding @Inject constructor() : ContributedBinding

//endregion

//region ContributedMultiBinding
interface ContributedMultiBinding

//region Set
@ContributesMultibinding(AppScope::class)
class FirstContributedMultibinding @Inject constructor() : ContributedMultiBinding

@ContributesMultibinding(AppScope::class)
class SecondContributedMultibinding @Inject constructor() : ContributedMultiBinding
//endregion

//region Map
@MapKey
annotation class BindingKey(val value: String)

interface Listener

@ContributesMultibinding(AppScope::class)
@BindingKey("listener")
class DefaultListener @Inject constructor() : Listener
//endregion
//endregion
