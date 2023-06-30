package baaahs.plugin.beatlink

data class TrackData(
    val id: Int,
    val title: String,
    val artist: String,
//    val art: BufferedImage?,
    val key: String,
    val cueList: List<Long>,
)
