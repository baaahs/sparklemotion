package baaahs.gadgets

import baaahs.imaging.Image
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface ImageRef {
    fun getImage(): Image

    @Serializable
    @SerialName("local")
    data class Local(val dataUrl: String) : ImageRef {
        override fun getImage(): Image = Image.fromDataUrl(dataUrl)
    }

    @Serializable
    @SerialName("show")
    data class Show(val imageId: String) : ImageRef {
        override fun getImage(): Image {
            TODO("not implemented")
        }
    }

    @Serializable
    @SerialName("server")
    data class Server(val imageId: String) : ImageRef {
        override fun getImage(): Image {
            TODO("not implemented")
        }
    }

    @Serializable
    @SerialName("video")
    data class Video(val imageId: String) : ImageRef {
        override fun getImage(): Image {
            TODO("not implemented")
        }
    }

}