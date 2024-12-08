enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()

        google {
            mavenContent {
                includeGroupAndSubgroups("android")
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }

        // KGL
        maven("https://maven.danielgergely.com/releases") {
            content { includeGroupAndSubgroups("com.danielgergely.kgl") }
        }

        // TarsosDSP
        maven("https://mvn.0110.be/releases") {
            content { includeGroupAndSubgroups("be.tarsos") }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
}

rootProject.name = "sparklemotion-root"

include("shared")
include("server")
include("ext:three")
include("rpc")
include("rpc:processor")
include("androidApp")
