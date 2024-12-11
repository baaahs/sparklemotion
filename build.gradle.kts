plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotestMultiplatform) apply false
}

val rootBuildDir = rootProject.layout.buildDirectory.get()
allprojects {
    val rootBuildDir = rootProject.layout.buildDirectory.get()
    val outputDirName = project.path.toString().trimStart(':').replace(":", "_")

    tasks.withType<Test> {
        // Copy in system properties.
        systemProperties = System.getProperties().asIterable().associate { it.key.toString() to it.value }

        reports.junitXml.outputLocation = rootProject.file("build/test-results/${outputDirName}")
        val outputDir = reports.junitXml.outputLocation.get()
        reports.junitXml.required.set(true)
        jvmArgumentProviders += CommandLineArgumentProvider {
            listOf(
                "-Djunit.platform.reporting.open.xml.enabled=true",
                "-Djunit.platform.reporting.output.dir=${outputDir.asFile.absolutePath}"
            )
        }
    }
}