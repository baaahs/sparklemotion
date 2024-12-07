plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

group = "org.baaahs"
version = "0.0.1"

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(19)
        vendor = JvmVendorSpec.ADOPTIUM
    }

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