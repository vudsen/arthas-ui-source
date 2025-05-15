pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "arthas-ui"
include("arthasui-api")
include("arthasui-core")
include("arthasui-common")
include("arthasui-bridge-impl")
include("arthasui-test")