package baaahs.libraries

import baaahs.PubSub
import baaahs.io.Fs
import baaahs.mapper.Storage
import baaahs.show.Shader
import baaahs.sm.webapi.SearchShaderLibraries
import baaahs.sm.webapi.Topics
import kotlinx.serialization.modules.SerializersModule

class ShaderLibraryManager(
    private val storage: Storage,
    pubSub: PubSub.Server
) {
    private lateinit var shaderLibraries: Map<Fs.File, ShaderLibrary>
    private val channel = pubSub.publish(
        Topics.createShaderLibraries(storage.fsSerializer), emptyMap()
    ) { updated ->
        // TODO: tbd.
    }

    init {
        pubSub.listenOnCommandChannel(Topics.Commands(SerializersModule {


        }).searchShaderLibraries) { command ->
            val matches = arrayListOf<ShaderLibrary.Entry>()
            shaderLibraries.forEach { (libRoot, shaderLibrary) ->
                shaderLibrary.entries.forEach { entry ->
                    if (entry.matches(command.terms)) {
                        matches.add(entry)
                    }
                }
            }
            SearchShaderLibraries.Response(matches)
        }
    }

    suspend fun start() {
        shaderLibraries = loadShaderLibraries().also {
            channel.onChange(it.mapKeys { it.key.fullPath })
        }
    }

    private suspend fun loadShaderLibraries(): Map<Fs.File, ShaderLibrary> {
        return storage.listShaderLibraries().associateWith { libDir ->
            loadShaderLibrary(libDir)
        }
    }

    private suspend fun loadShaderLibrary(libDir: Fs.File): ShaderLibrary {
        return storage.loadShaderLibraryIndexFile(libDir).let { indexFile ->
            ShaderLibrary(
                indexFile.title,
                indexFile.description,
                indexFile.license,
                indexFile.entries.map { fileEntry ->
                    val src = libDir.resolve(fileEntry.srcFile).read()!!

                    ShaderLibrary.Entry(
                        fileEntry.id,
                        Shader(fileEntry.title, src),
                        fileEntry.description,
                        fileEntry.lastModifiedMs,
                        fileEntry.tags
                    )
                }
            )
        }
    }
}