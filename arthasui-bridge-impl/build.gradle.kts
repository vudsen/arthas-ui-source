dependencies {
    testImplementation(kotlin("test"))
    api(project(":arthasui-api"))
    api(project(":arthasui-common"))
    implementation("org.apache.sshd:sshd-core:2.14.0") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("commons-net:commons-net:3.11.1") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    intellijPlatform {
        intellijIdeaCommunity(providers.gradleProperty("platformVersion"))
    }
}
group = "io.github.vudsen.arthasui.bridge"
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