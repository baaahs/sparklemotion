pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "sparklemotion"
include("net")
include("net:fixtures")
include("rpc")
include("rpc:processor")
include("util")
