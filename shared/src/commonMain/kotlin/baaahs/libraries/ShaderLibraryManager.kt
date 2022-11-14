package baaahs.libraries

import baaahs.PubSub
import baaahs.client.document.DataStore
import baaahs.gl.Toolchain
import baaahs.gl.openShader
import baaahs.hyphenize
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
import baaahs.util.UniqueIds
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

class ShaderLibraryManager(
    plugins: Plugins,
    private val fs: Fs,
    fsSerializer: FsServerSideSerializer,
    pubSub: PubSub.Server,
    private val toolchain: Toolchain
) {
    private val shaderLibraryIndexStore =
        DataStore(plugins, ShaderLibraryIndexDataMigrator())

    private lateinit var shaderLibraries: Map<Fs.File, ShaderLibrary>
    private val channel = pubSub.publish(
        Topics.createShaderLibraries(fsSerializer), emptyMap()
    ) { updated ->
        // TODO: tbd.
    }
    private val prettyJson = Json(plugins.json) {
        prettyPrint = true
        @OptIn(ExperimentalSerializationApi::class)
        prettyPrintIndent = "  "
    }

    init {
        Topics.shaderLibrariesCommands.createReceiver(pubSub, object : ShaderLibraryCommands {
            override suspend fun search(terms: String): List<ShaderLibrary.Entry> =
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
        })
    }

    private val shaderLibrariesPath = MergedFs(fs, resourcesFs)
        .resolve("shader-libraries")

    private suspend fun listShaderLibraries(): List<Fs.File> {
        return shaderLibrariesPath.listFiles()
            .also { logger.debug { "shader libraries: $it" } }
            .filter { it.libraryIndexFile().exists() }
            .also { logger.debug { "shader libraries with index: $it" } }
    }

    suspend fun saveShaderLibraryIndexFile(libraryName: String, index: ShaderLibraryIndex) {
        val asJson = prettyJson.encodeToString(ShaderLibraryIndex.serializer(), index)
        shaderLibraryPath(libraryName).libraryIndexFile()
            .write(asJson, true)
    }
    suspend fun listShaderLibraryFiles(libraryName: String) : List<Fs.File> =
        shaderLibraryPath(libraryName).listFiles()
    fun shaderLibraryPath(libraryName: String) =
        shaderLibrariesPath.resolve(libraryName)

    private suspend fun loadShaderLibraryIndexFile(libDir: Fs.File): ShaderLibraryIndex =
        shaderLibraryIndexStore.load(libDir.libraryIndexFile())!!

    private fun Fs.File.libraryIndexFile() = resolve("_libraryIndex.json", isDirectory = false)


    suspend fun start() {
        shaderLibraries = loadShaderLibraries().also {
            channel.onChange(it.mapKeys { it.key.fullPath })
        }
    }

    suspend fun buildIndex(libraryName: String) {
        val uniqueIds = UniqueIds<Fs.File>()

        val shaderLibraryPath = shaderLibraryPath(libraryName)
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
                }
                ShaderLibraryIndex.Entry(
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
                ShaderLibraryIndex.Entry(
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

        val index = ShaderLibraryIndex(
            libraryName,
            null,
            null,
            entries.sortedBy { it.id.lowercase() }
        )
        logger.info { "Indexed $countOk/$countTotal shaders." }
        saveShaderLibraryIndexFile(libraryName, index)
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
