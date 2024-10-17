plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
}

group = "org.baaahs"
version = "0.0.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(19)
        vendor = JvmVendorSpec.ADOPTIUM
    }
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(19)
        vendor = JvmVendorSpec.ADOPTIUM
    }

    androidTarget {}

    sourceSets {
        commonMain {
            dependencies {
                api(projects.shared)
            }
        }

        androidMain {
            dependencies {
                api(projects.shared)
                implementation(libs.androidCoreKtx)
                implementation(libs.koinCore)
                implementation(libs.kotlinxDatetime)
                implementation(libs.kotlinxCoroutinesAndroid)
                implementation(libs.ktorServerCore)
                implementation(libs.ktorServerNetty)
                implementation(libs.ktorServerHostCommon)
                implementation(libs.ktorServerCallLogging)
                implementation(libs.ktorServerWebsockets)
            }
        }

//        sourceSets.all {
//            languageSettings.apply {
//                progressiveMode = true
//                optIn("kotlin.ExperimentalStdlibApi")
//            }
//        }
    }
}

afterEvaluate {
    tasks.named("mergeDebugAssets").configure {
        dependsOn(":shared:jsBrowserDevelopmentWebpack")
    }
}

android {
    namespace = "baaahs.app.android"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
//    useAndroidX()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/androidMain/resources",
        rootProject.file("shared/src/jsMain/resources"),
        rootProject.file("shared/src/commonMain/resources"),
        rootProject.file("shared/build/kotlin-webpack/js/developmentExecutable")
    )
    sourceSets["main"].assets.srcDirs(
        rootProject.file("shared/src/jsMain/resources"),
        rootProject.file("shared/src/commonMain/resources"),
        rootProject.file("shared/build/kotlin-webpack/js/developmentExecutable")
    )

    println("htdocs=${rootProject.file("shared/src/jsMain/resources")}")
    defaultConfig {
        applicationId = "baaahs.sparklemotion"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{INDEX.LIST,AL2.0,LGPL2.1,io.netty.versions.properties}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        viewBinding = true
    }
}