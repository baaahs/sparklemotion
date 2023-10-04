package baaahs.gl.patch

import baaahs.show.Shader
import baaahs.show.mutable.MutablePatchSet
import baaahs.util.Logger

class UnresolvedPatches(private val unresolvedPatches: List<UnresolvedPatch>) {
    fun find(predicate: (UnresolvedPatch) -> Boolean) =
        unresolvedPatches.find(predicate)

    fun editShader(title: String, callback: UnresolvedPatch.() -> Unit): UnresolvedPatches =
        editShader({ it.mutableShader.title == title }, callback)

    fun editShader(predicate: (UnresolvedPatch) -> Boolean, callback: UnresolvedPatch.() -> Unit): UnresolvedPatches {
        val match = find(predicate)
            ?: error("Couldn't find shader.")
        match.callback()
        return this
    }

    fun editShader(shader: Shader, callback: UnresolvedPatch.() -> Unit): UnresolvedPatches {
        // TODO: src == src is probably the wrong check here:
        val match = find { it.mutableShader.src == shader.src }
            ?: error("Couldn't find shader \"${shader.title}\"")
        match.callback()
        return this
    }

    fun editAll(callback: UnresolvedPatch.() -> Unit): UnresolvedPatches {
        unresolvedPatches.forEach { it.callback() }
        return this
    }

    fun isAmbiguous() = unresolvedPatches.any { it.isAmbiguous() }

    fun confirm(): MutablePatchSet {
        if (isAmbiguous()) {
            error("ambiguous! " +
                    unresolvedPatches
                        .filter { it.isAmbiguous() }
                        .map { it.describeAmbiguity() }
            )
        }

        // Create a patch editor for each shader.
        val mutablePatches = unresolvedPatches.associate {
            it.mutableShader.build() to it.confirm()
        }

        return MutablePatchSet(mutablePatches.values.toMutableList())
    }

    fun dumpOptions(): UnresolvedPatches {
        logger.info { "Unresolved Patch:" }
        unresolvedPatches.forEach { unresolvedPatch ->
            logger.info { "* ${unresolvedPatch.mutableShader.title}" }
            unresolvedPatch.incomingLinksOptions.forEach { (inputPort, linkOptions) ->
                logger.info { "  ${inputPort.id} (${inputPort.contentType}) ->" }
                linkOptions.forEach { linkOption ->
                    logger.info { "    * ${linkOption.title}"}
                }
            }
        }
        return this
    }

    fun acceptSuggestedLinkOptions(): UnresolvedPatches {
        unresolvedPatches.forEach { it.takeFirstIfAmbiguous() }
        return this
    }

    companion object {
        private val logger = Logger<UnresolvedPatch>()
    }
}
