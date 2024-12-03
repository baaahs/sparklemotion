package baaahs.fixtures

import baaahs.device.FixtureType
import baaahs.model.Model
import baaahs.scene.MutableFixtureOptions

/**
 * Configuration options for a fixture whose values may be null, so options
 * may be merged into a [FixtureConfig].
 */
interface FixtureOptions {
    val componentCount: Int?
    val bytesPerComponent: Int

    val fixtureType: FixtureType

    fun edit(): MutableFixtureOptions

    /** Merges two options, preferring values from [other]. */
    operator fun plus(other: FixtureOptions?): FixtureOptions

    fun preview(): ConfigPreview

    /** Create a non-nullable version of the current options, throwing exceptions for missing values. */
    fun toConfig(entity: Model.Entity?, model: Model, defaultComponentCount: Int? = null): FixtureConfig
}

fun FixtureOptions?.merge(options: FixtureOptions): FixtureOptions? =
    when {
        this == null -> options
        this::class == options::class -> this + options
        else -> this
    }

/** Finalized configuration for a fixture. */
interface FixtureConfig {
    val componentCount: Int
    val bytesPerComponent: Int

    val fixtureType: FixtureType
}