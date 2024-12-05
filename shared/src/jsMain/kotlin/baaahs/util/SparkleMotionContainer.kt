package baaahs.util

external interface SparkleMotionContainer {
    val platform: String
    val urlBase: Location?
}

external interface Location {
    val protocol: String
    val hostname: String
    val port: Int
}