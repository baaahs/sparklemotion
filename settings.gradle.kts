pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
}

rootProject.name = "sparklemotion"

include("shared")
include("ext:three")
include("rpc")
include("rpc:processor")
