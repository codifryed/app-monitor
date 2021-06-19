import org.jetbrains.changelog.closure
import org.jetbrains.changelog.date

plugins {
    id("org.jetbrains.intellij") version "0.7.3"
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.10"
    id("org.jetbrains.changelog") version "1.1.2"
}

group = "com.guyboldon"
version = "0.1.0"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.jetbrains.kotlin", "kotlin-stdlib")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.5.0")
    implementation("io.ktor", "ktor-client-core", "1.5.4")
    implementation("io.ktor", "ktor-client-cio", "1.5.4")
    implementation("io.ktor", "ktor-client-mock", "1.5.4")
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.2.1")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2021.1.1"
}

tasks {
    patchPluginXml {
        setVersion(version)
        setPluginDescription(file("includes/pluginDescription.html").readText())
        changeNotes(closure { changelog.getLatest().toHTML() })
    }
    compileKotlin {
        kotlinOptions { jvmTarget = "11" }
    }
}


changelog {
    version = "${project.version}"
    path = "${project.projectDir}/CHANGELOG.md"
    header = closure { "[$version] - ${date()}" }
    // itemPrefix = "-"
    // keepUnreleasedSection = true
    unreleasedTerm = "[Unreleased]"
    groups = listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security")
}