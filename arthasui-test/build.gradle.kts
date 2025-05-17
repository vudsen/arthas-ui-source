kotlin {
    jvmToolchain(17)
}

group = "io.github.vudsen.arthasui.test"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation(libs.testContainers)
    implementation(project(":arthasui-common"))
    implementation(project(":arthasui-bridge-impl"))
    api(project(":arthasui-api"))
    intellijPlatform {
        intellijIdeaCommunity(providers.gradleProperty("platformVersion"))
    }
}

tasks.test {
    useJUnitPlatform()
}

plugins {
    id("org.jetbrains.intellij.platform.module") version "2.3.0"
}
