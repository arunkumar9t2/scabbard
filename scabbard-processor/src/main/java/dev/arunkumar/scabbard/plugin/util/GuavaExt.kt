@file:Suppress("UnstableApiUsage")

package dev.arunkumar.scabbard.plugin.util

import com.google.common.graph.EndpointPair

operator fun <N> EndpointPair<N>.component1(): N = source()
operator fun <N> EndpointPair<N>.component2(): N = target()