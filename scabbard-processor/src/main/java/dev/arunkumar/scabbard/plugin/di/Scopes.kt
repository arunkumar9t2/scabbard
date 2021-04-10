package dev.arunkumar.scabbard.plugin.di

import dagger.spi.BindingGraphPlugin
import javax.inject.Scope

/**
 * Dagger scope lasting the duration of entire processor run
 */
@Scope
@Retention
@MustBeDocumented
annotation class ProcessorScope

/**
 * Dagger scope lasting for the duration of [BindingGraphPlugin.visitGraph].
 */
@Scope
@Retention
@MustBeDocumented
annotation class VisitGraphScope
