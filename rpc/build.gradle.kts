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

    iosArm64()
    iosX64()
    iosSimulatorArm64()

    explicitApi()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinxSerializationJson)
            }
        }
    }
}