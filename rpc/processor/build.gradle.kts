plugins {
    kotlin("jvm")
}

group = "org.baaahs"
version = "0.0.1"

repositories {
    mavenCentral()
}

kotlin {
}

dependencies {
    implementation(projects.rpc)
    implementation(libs.kspSymbolProcessingApi)
    testImplementation(libs.kotlinTest)
}

tasks.test {
    useJUnitPlatform()
}
