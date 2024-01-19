plugins {
    kotlin("multiplatform") version Versions.kotlin
}

group = "org.baaahs"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://raw.githubusercontent.com/robolectric/spek/mvnrepo/")
}

kotlin {
    jvm()
    js(IR) {
        browser()
    }

    explicitApi()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":net"))
                implementation(kotlinx("coroutines-core", Versions.coroutines))
                implementation(kotlinx("serialization-json", Versions.serializationRuntime))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlinx("coroutines-test", Versions.coroutines))
                implementation("spek:spek-dsl:${Versions.spek}")
                implementation("ch.tutteli.atrium:${Versions.atriumApi}-common:${Versions.atrium}")
            }
        }

        val jvmTest by getting {
            dependencies {
                runtimeOnly("org.spekframework.spek2:spek-runner-junit5:${Versions.spek}")
                implementation(project.dependencies.platform("org.junit:junit-bom:${Versions.junit}"))
                implementation("ch.tutteli.atrium:${Versions.atriumApi}:${Versions.atrium}")
            }
        }
    }
}

tasks.withType(Test::class) {
    useJUnitPlatform {
        includeEngines.add("junit-jupiter")
        includeEngines.add("spek2")
        excludeTags("glsl")
    }
}