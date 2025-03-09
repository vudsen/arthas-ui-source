sourceSets {
    main {
        java {
            srcDirs("src/main/gen")
        }
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.11.0")
    implementation(project(":arthasui-common"))
    implementation(project(":arthasui-bridge-impl"))
    api(project(":arthasui-api"))
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
        untilBuild.set("242.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
