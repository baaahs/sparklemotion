pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "sparklemotion"

include("ext:three")
include("rpc")
include("rpc:processor")
