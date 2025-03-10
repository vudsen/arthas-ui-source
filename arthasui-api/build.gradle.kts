dependencies {
    testImplementation(kotlin("test"))
    intellijPlatform {
        intellijIdeaCommunity(providers.gradleProperty("platformVersion"))
    }
}
group = "io.github.vudsen.arthasui.api"
tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

plugins {
    id("org.jetbrains.intellij.platform.module") version "2.3.0"
}