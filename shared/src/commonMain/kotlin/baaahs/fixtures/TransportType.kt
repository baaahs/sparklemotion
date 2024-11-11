package baaahs.fixtures

interface TransportType {
    val id: String
    val title: String
    val emptyConfig: TransportConfig
    val isConfigurable: Boolean
}