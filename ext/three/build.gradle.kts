plugins {
    kotlin("multiplatform") version Versions.kotlin
}

group = "org.baaahs"
version = "0.0.1"

fun kotlinw(target: String): String =
    "org.jetbrains.kotlin-wrappers:kotlin-$target:${Versions.kotlinWrappers}"

repositories {
    mavenCentral()
}

kotlin {
    js(IR) {
        browser()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(kotlinw("browser-js"))

                implementation(npm("three", "^0.168.0"))
                implementation(npm("@types/three", "^0.168.0"))
            }
        }
    }
}