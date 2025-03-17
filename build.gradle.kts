plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "java")

    version = providers.gradleProperty("pluginVersion").get()

    repositories {
        mavenCentral()
    }
}
