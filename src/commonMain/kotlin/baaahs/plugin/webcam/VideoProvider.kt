package baaahs.plugin.webcam

import com.danielgergely.kgl.TextureResource

interface VideoProvider {
    fun getTextureResource(): TextureResource
}

expect val DefaultVideoProvider : VideoProvider