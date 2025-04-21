dependencies {
    testImplementation(kotlin("test"))
    api(project(":arthasui-api"))
    api(project(":arthasui-common"))
    implementation("org.apache.sshd", "sshd-core", libs.versions.minaSshd.get()) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("org.apache.sshd", "sshd-sftp", libs.versions.minaSshd.get()) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("commons-net:commons-net:3.11.1") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    intellijPlatform {
        intellijIdeaCommunity(providers.gradleProperty("platformVersion"))
    }

    implementation("com.google.code.gson:gson:2.11.0")
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