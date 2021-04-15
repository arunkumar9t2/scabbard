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
