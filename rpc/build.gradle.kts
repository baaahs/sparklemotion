plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

group = "org.baaahs"
version = "0.0.1"

kotlin {
    jvm()
    js(IR) {
        browser()
    }

    explicitApi()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
            }
        }
    }
}