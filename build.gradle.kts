plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "java")

    version = "0.0.3-alpha"

    repositories {
        maven{ url=uri("https://maven.aliyun.com/repository/public") }
        maven{ url=uri("https://maven.aliyun.com/repository/google") }
    }



}
