package baaahs.visualizer.geometry

class EdgeNeighbors(faceInfos: List<FaceInfo>) {
    val byLineSegment: Map<String, List<FaceInfo>> = run {
        mutableMapOf<String, MutableList<FaceInfo>>().also { map ->
            faceInfos.forEach { faceInfo ->
                faceInfo.segments.forEach { segment ->
                    map.getOrPut(segment.key) { mutableListOf() }
                        .add(faceInfo)
                }
            }
        }
    }

    fun find(segment: FaceInfo.LineSegment): List<FaceInfo> =
        byLineSegment[segment.key] ?: emptyList()
}