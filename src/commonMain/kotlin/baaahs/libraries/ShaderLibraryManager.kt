package baaahs.libraries

import baaahs.PubSub
import baaahs.client.document.DataStore
import baaahs.io.Fs
import baaahs.io.FsServerSideSerializer
import baaahs.io.resourcesFs
import baaahs.migrator.DataMigrator
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.sim.MergedFs
import baaahs.sm.webapi.ShaderLibraryCommands
import baaahs.sm.webapi.Topics
import baaahs.util.Logger

class ShaderLibraryManager(
    plugins: Plugins,
    private val fs: Fs,
    fsSerializer: FsServerSideSerializer,
    pubSub: PubSub.Server
) {
    private val shaderLibraryIndexStore =
        DataStore(plugins, ShaderLibraryIndexDataMigrator())

    private lateinit var shaderLibraries: Map<Fs.File, ShaderLibrary>
    private val channel = pubSub.publish(
        Topics.createShaderLibraries(fsSerializer), emptyMap()
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

    private suspend fun listShaderLibraries(): List<Fs.File> {
        return MergedFs(fs, resourcesFs)
            .resolve("shader-libraries").listFiles()
            .also { logger.debug { "shader libraries: $it" } }
            .filter { it.libraryIndexFile().exists() }
            .also { logger.debug { "shader libraries with index: $it" } }
    }

    private suspend fun loadShaderLibraryIndexFile(libDir: Fs.File): ShaderLibraryIndex =
        shaderLibraryIndexStore.load(libDir.libraryIndexFile())!!

    private fun Fs.File.libraryIndexFile() = resolve("_libraryIndex.json", isDirectory = false)


    suspend fun start() {
        shaderLibraries = loadShaderLibraries().also {
            channel.onChange(it.mapKeys { it.key.fullPath })
        }
    }

    private suspend fun loadShaderLibraries(): Map<Fs.File, ShaderLibrary> {
        return listShaderLibraries().associateWith { libDir ->
            loadShaderLibrary(libDir)
        }
    }

    private suspend fun loadShaderLibrary(libDir: Fs.File): ShaderLibrary {
        return loadShaderLibraryIndexFile(libDir).let { indexFile ->
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

    class ShaderLibraryIndexDataMigrator :
        DataMigrator<ShaderLibraryIndex>(ShaderLibraryIndex.serializer())

    companion object {
        private val logger = Logger<ShaderLibraryManager>()
    }
}