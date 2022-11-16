package baaahs.libraries

import baaahs.PubSub
import baaahs.gl.Toolchain
import baaahs.gl.openShader
import baaahs.hyphenize
import baaahs.io.Fs
import baaahs.mapper.Storage
import baaahs.show.Shader
import baaahs.sm.webapi.SearchShaderLibraries
import baaahs.sm.webapi.Topics
import baaahs.util.Logger
import baaahs.util.UniqueIds
import kotlinx.serialization.modules.SerializersModule

class ShaderLibraryManager(
    private val storage: Storage,
    pubSub: PubSub.Endpoint,
    private val toolchain: Toolchain
) {
    private lateinit var shaderLibraries: Map<Fs.File, ShaderLibrary>
    private val channel = pubSub.openChannel(
        Topics.createShaderLibraries(storage.fsSerializer), emptyMap()
    ) { updated ->
        // TODO: tbd.
    }

    inner class Facade : ShaderLibraries {
        override suspend fun searchFor(terms: String): List<ShaderLibrary.Entry> {
            return this@ShaderLibraryManager.searchFor(terms)
        }
    }

    init {
        pubSub.listenOnCommandChannel(Topics.Commands(SerializersModule {}).searchShaderLibraries) { command ->
            val matches = searchFor(command.terms)
            SearchShaderLibraries.Response(matches)
        }
    }

    fun searchFor(terms: String): List<ShaderLibrary.Entry> =
        buildList {
            shaderLibraries.forEach { (_, shaderLibrary) ->
                val libId = shaderLibrary.title.hyphenize()
                shaderLibrary.entries.forEach { entry ->
                    if (entry.matches(terms)) {
                        add(entry.copy(id = "${libId}:${entry.id}"))
                    }
                }
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
        logger.info { "Indexing shader library at \"${shaderLibraryPath.fullPath}\"."}
        var countOk = 0
        var countTotal = 0
        val entries = shaderLibraryPath.listFilesRecursively().mapNotNull { file ->
            countTotal++
//            if (Regex("\\.(png|jpg|gif|webp|txt|md|json)\$").containsMatchIn(file.name))
//                return@mapNotNull null
            if (!Regex("\\.(fs|glsl)\$").containsMatchIn(file.name))
                return@mapNotNull null
            if (file.fs.isDirectory(file))
                return@mapNotNull null

            val fileRelPath = file.relativeTo(shaderLibraryPath)
            try {
                val contents = file.read()!!
                val shader = toolchain.import(contents, file.name)
                val analysis = toolchain.analyze(shader)
                val openShader = toolchain.openShader(shader)
//                println("${file.name}: $analysis")
                val tags = buildList {
                    add("@type=${openShader.shaderType.title}")
                    if (openShader.isFilter) add("@filter")
                    addAll(shader.tags)
                }
                ShaderLibraryIndexFile.Entry(
                    uniqueIds.idFor(file) { shader.title.hyphenize() },
                    shader.title,
                    shader.description,
                    null,
                    tags,
                    fileRelPath,
                    analysis.errors.map { it.toString() }
                ).also { countOk++ }
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
            entries.sortedBy { it.id.lowercase() }
        )
        logger.info { "Indexed $countOk/$countTotal shaders." }
        storage.saveShaderLibraryIndexFile(libraryName, index)
    }

    private suspend fun Fs.File.listFilesRecursively(): List<Fs.File> = buildList {
        listFiles().forEach {
            if (it.isDir()) {
                addAll(it.listFilesRecursively())
            } else {
                add(it)
            }
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

    companion object {
        private val logger = Logger<ShaderLibraryManager>()
    }
}