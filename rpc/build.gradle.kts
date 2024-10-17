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
        commonMain {
            dependencies {
                implementation(libs.kotlinxSerializationJson)
            }
        }
    }
}