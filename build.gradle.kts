plugins {
    id("org.jetbrains.intellij") version "0.7.3"
    kotlin("jvm") version "1.5.0"
    kotlin("plugin.serialization") version "1.5.0"
}

group = "com.guyboldon"
version = "0.1"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    implementation(group = "io.ktor", name = "ktor-client-core", version = "1.5.4")
    implementation(group = "io.ktor", name = "ktor-client-cio", version = "1.5.4")
    implementation(group = "io.ktor", name = "ktor-client-mock", version = "1.5.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2021.1.1"
}

tasks.patchPluginXml {
    changeNotes(
        """
            0.1 - Initial Release<br>
        """
    )
}

tasks.compileKotlin {
    kotlinOptions { jvmTarget = "11" }
}