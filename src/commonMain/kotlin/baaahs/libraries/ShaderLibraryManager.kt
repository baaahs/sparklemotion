package baaahs.libraries

import baaahs.PubSub
import baaahs.camelize
import baaahs.gl.Toolchain
import baaahs.io.Fs
import baaahs.mapper.Storage
import baaahs.show.Shader
import baaahs.sm.webapi.SearchShaderLibraries
import baaahs.sm.webapi.Topics
import baaahs.util.UniqueIds
import kotlinx.serialization.modules.SerializersModule

class ShaderLibraryManager(
    private val storage: Storage,
    pubSub: PubSub.Server,
    private val toolchain: Toolchain
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
            shaderLibraries.forEach { (_, shaderLibrary) ->
                val libId = shaderLibrary.title.camelize()
                shaderLibrary.entries.forEach { entry ->
                    if (entry.matches(command.terms)) {
                        matches.add(entry.copy(id = "${libId}:${entry.id}"))
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

    suspend fun buildIndex(libraryName: String) {
        val uniqueIds = UniqueIds<Fs.File>()

        val shaderLibraryPath = storage.shaderLibraryPath(libraryName)
        val entries = shaderLibraryPath.listFiles().mapNotNull { file ->
            if (Regex("\\.(png|jpg|gif|webp|txt|md|json)\$").containsMatchIn(file.name))
                return@mapNotNull null

            val fileRelPath = file.relativeTo(shaderLibraryPath)
            try {
                val contents = file.read()!!
                val shader = toolchain.import(contents, file.name)
                val analysis = toolchain.analyze(shader)
//                println("${file.name}: $analysis")
                ShaderLibraryIndexFile.Entry(
                    uniqueIds.idFor(file) { analysis.shader.suggestId() },
                    analysis.shader.title,
                    null,
                    null,
                    emptyList(),
                    fileRelPath,
                    analysis.errors.map { it.toString() }
                )
            } catch (e: Exception) {
//                println("Problem analyzing ${file.name}.")
//                e.printStackTrace()
                ShaderLibraryIndexFile.Entry(
                    uniqueIds.idFor(file) { file.name },
                    file.name,
                    null,
                    null,
                    emptyList(),
                    fileRelPath,
                    listOf(e.message!!)
                )
            }
        }

        val index = ShaderLibraryIndexFile(
            libraryName,
            null,
            null,
            entries
        )
        storage.saveShaderLibraryIndexFile(libraryName, index)
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