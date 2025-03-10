sourceSets {
    main {
        java {
            srcDirs("src/main/gen")
        }
    }
}

repositories {
    intellijPlatform {
        defaultRepositories()

    }
}

plugins {
    id("org.jetbrains.intellij.platform") version "2.3.0"
}

dependencies {
    implementation("com.google.code.gson:gson:2.11.0")
    intellijPlatform {
        intellijIdeaCommunity(providers.gradleProperty("platformVersion"))
        bundledPlugin("com.intellij.java")
        pluginModule(implementation(project(":arthasui-common")))
        pluginModule(implementation(project(":arthasui-bridge-impl")))
        pluginModule(api(project(":arthasui-api")))
        pluginVerifier()
        zipSigner()
    }
}

group = "io.github.vudsen.arthasui"

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("243.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

//    intellijPlatform {
//        pluginVerification {
//            ides
//        }
//    }
}
