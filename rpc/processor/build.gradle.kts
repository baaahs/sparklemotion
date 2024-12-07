plugins {
    kotlin("jvm")
}

group = "org.baaahs"
version = "0.0.1"

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(19)
        vendor = JvmVendorSpec.ADOPTIUM
    }
}

dependencies {
    implementation(projects.rpc)
    implementation(libs.kspSymbolProcessingApi)
    testImplementation(libs.kotlinTest)
}

tasks.test {
    useJUnitPlatform()
}
