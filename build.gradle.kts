import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.20"
    id("org.jetbrains.dokka") version "2.0.0"
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "java")

    val sha = providers.environmentVariable("HEAD_SHA").orNull
    if (sha == null || sha.isEmpty()) {
        version = providers.gradleProperty("pluginVersion").get()
    } else {
        version = providers.gradleProperty("pluginVersion").get() + "-$sha"
    }

    repositories {
        maven{ url=uri("https://maven.aliyun.com/repository/public") }
        maven{ url=uri("https://maven.aliyun.com/repository/google") }
    }
}

tasks {
    withType<DokkaTask>().configureEach {
        dokkaSourceSets {
            named("main") {
                // used as project name in the header
                moduleName.set("Arthas UI")
                includes.from("Module.md")

                sourceRoots.setFrom(
                    "arthasui-core/src/main/kotlin/io/github/vudsen/arthasui/script",
                    "arthasui-api/src/main/kotlin/io/github/vudsen/arthasui/api",
                    "arthasui-bridge-impl/src/main/kotlin/io/github/vudsen/arthasui/bridge/conf/LocalJvmProviderConfig.kt"
                )
            }
        }
    }
}