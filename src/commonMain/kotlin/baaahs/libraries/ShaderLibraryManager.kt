package baaahs.libraries

import baaahs.PubSub
import baaahs.io.Fs
import baaahs.mapper.Storage
import baaahs.show.Shader
import baaahs.sm.webapi.ShaderLibraryCommands
import baaahs.sm.webapi.Topics

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
        Topics.shaderLibrariesCommands.createReceiver(pubSub, object : ShaderLibraryCommands {
            override suspend fun search(terms: String): List<ShaderLibrary.Entry> =
                buildList {
                    shaderLibraries.forEach { (_, shaderLibrary) ->
                        shaderLibrary.entries.forEach { entry ->
                            if (entry.matches(terms)) add(entry)
                        }
                    }
                }
        })
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
                    val src = libDir.resolve(fileEntry.srcFile).read()
                        ?: error("No such file \"${fileEntry.srcFile}\" in shader library \"${indexFile.title}\".")

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