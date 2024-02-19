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
    implementation(project(":rpc"))
    implementation("com.google.devtools.ksp:symbol-processing-api:${Versions.ksp}")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
