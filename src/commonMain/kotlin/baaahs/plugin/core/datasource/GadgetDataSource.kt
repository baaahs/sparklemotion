package baaahs.plugin.core.datasource

import baaahs.Gadget
import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.ShowPlayer
import baaahs.control.MutableGadgetControl
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.Feed
import baaahs.gl.data.ProgramFeed
import baaahs.gl.data.SingleUniformFeed
import baaahs.gl.glsl.GlslProgram
import baaahs.glsl.Uniform
import baaahs.plugin.core.CorePlugin
import baaahs.show.DataSource
import baaahs.util.Logger
import kotlinx.serialization.SerialName

interface GadgetDataSource<T : Gadget> : DataSource {
    @SerialName("title")
    val gadgetTitle: String

    override fun buildControl(): MutableGadgetControl {
        return MutableGadgetControl(createGadget(), this)
    }

    fun createGadget(): T

    fun set(gadget: T, uniform: Uniform)

    override fun createFeed(showPlayer: ShowPlayer, id: String): Feed {
        val gadget = showPlayer.useGadget<T>(this)
            ?: run {
                logger.debug { "No control gadget registered for datasource $id, creating one. This is probably busted." }
                createGadget()
            }

        return object : CorePlugin.GadgetFeed, RefCounted by RefCounter() {
            override val id: String = id
            override val gadget: Gadget = gadget

            override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                override fun bind(glslProgram: GlslProgram): ProgramFeed {
                    return SingleUniformFeed(glslProgram, this@GadgetDataSource, id) { uniform ->
                        this@GadgetDataSource.set(gadget, uniform)
                    }
                }
            }

            override fun release() = Unit
        }
    }

    companion object {
        private val logger = Logger<GadgetDataSource<*>>()
    }
}