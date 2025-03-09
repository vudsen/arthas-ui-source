plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.17.4"
}

allprojects {
    apply(plugin = "org.jetbrains.intellij")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "java")

    version = "0.0.1-alpha"

    repositories {
        maven{ url=uri("https://maven.aliyun.com/repository/public") }
        maven{ url=uri("https://maven.aliyun.com/repository/google") }
    }

    intellij {
        version.set("2023.2.6")
        type.set("IC") // Target IDE Platform

        plugins.set(listOf("com.intellij.java"))
    }

}