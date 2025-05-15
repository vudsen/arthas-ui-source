import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.models.ProductInfo
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.io.FileInputStream
import java.io.FileOutputStream

dependencies {
    implementation("ognl:ognl:3.4.6")
    testImplementation(kotlin("test"))
    testImplementation(project(":arthasui-test"))
    testImplementation(libs.testContainers)
    testImplementation(libs.kubernetesClient)

    intellijPlatform {
        intellijIdeaCommunity(providers.gradleProperty("platformVersion"))
        bundledPlugins("com.intellij.java")

        pluginModule(implementation(project(":arthasui-common")))
        pluginModule(implementation(project(":arthasui-bridge-impl")))
        pluginModule(api(project(":arthasui-api")))
        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Platform)
        testFramework(TestFrameworkType.Starter)
    }
}

sourceSets {
    main {
        java {
            srcDirs("src/main/gen")
        }
    }
    create("integrationTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

repositories {
    intellijPlatform {
        defaultRepositories()
    }
}

plugins {
    alias(libs.plugins.intelliJPlatform)
    alias(libs.plugins.changelog)
}

val integrationTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

val platformInternalVersion = providers.gradleProperty("platformInternalVersion").get()

dependencies {
    integrationTestImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    integrationTestImplementation("org.kodein.di:kodein-di-jvm:7.20.2")
    integrationTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.1")
    integrationTestImplementation("com.jetbrains.intellij.java", "java-rt", platformInternalVersion)
    integrationTestImplementation(project(":arthasui-test"))
    integrationTestImplementation(libs.testContainers)
}


val integrationTest = task<Test>("integrationTest") {
    if (ProductInfo.Launch.OS.current == ProductInfo.Launch.OS.Linux) {
        // avoid download twice
        val file = File(
            rootProject.projectDir.absolutePath + "/out/ide-tests/installers/IC/ideaIC-${
                providers.gradleProperty("platformInternalVersion").get()
            }.tar.gz"
        )
        if (!file.exists()) {
            val deps = dependencies.create("idea", "ideaIC", providers.gradleProperty("platformVersion").get(), ext = "tar.gz")
            val files = configurations.detachedConfiguration(deps).files
            file.parentFile.mkdirs()
            val source = files.first()
            logger.info("Copying ${source.absolutePath} to ${file.absolutePath}")
            FileInputStream(source).channel.use { from ->
                FileOutputStream(file).channel.use { to ->
                    from.transferTo(0, from.size(), to);
                }
            }
        }
    }
    // intellij.test.jars.location=/root/.gradle/
    //idea.home.path=/root/project/arthas-ui-new/out/ide-tests/cache/builds/IC-243.26053.27/idea-IC-243.26053.27

    val squashed = dependencies.create("com.jetbrains.intellij.tools", "ide-starter-squashed", platformInternalVersion, ext = "jar")
    systemProperty("intellij.test.jars.location", configurations.detachedConfiguration(squashed).files.first().absolutePath)
    systemProperty("idea.home.path", "${project.projectDir.absolutePath}/out/ide-tests/cache/builds/IC-${platformInternalVersion}/idea-IC-${platformInternalVersion}")

    systemProperty("platformVersion", providers.gradleProperty("platformVersion").get())
    val integrationTestSourceSet = sourceSets.getByName("integrationTest")
    testClassesDirs = integrationTestSourceSet.output.classesDirs
    classpath = integrationTestSourceSet.runtimeClasspath
    systemProperty("path.to.build.plugin", tasks.prepareSandbox.get().pluginDirectory.get().asFile)
    useJUnitPlatform()
    dependsOn(tasks.prepareSandbox)
}

group = "io.github.vudsen.arthasui"

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "241"
            untilBuild = "251.*"
        }

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        description = providers.fileContents(layout.projectDirectory.file("../README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        val changelog = project.changelog // local variable for configuration cache compatibility
        // Get the latest available change notes from the changelog file
        changeNotes = providers.gradleProperty("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }


    }

    signing {
        certificateChainFile = layout.projectDirectory.file("certificate/chain.crt")
        privateKeyFile = layout.projectDirectory.file("certificate/private_encrypted.pem")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }


    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels = providers.gradleProperty("pluginVersion").map { listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" }) }
    }

    pluginVerification {
        ides {
            val productReleases = ProductReleasesValueSource().get()
            val reducedProductReleases =
                if (productReleases.size > 2) listOf(productReleases.first(), productReleases.last())
                else productReleases
            ides(reducedProductReleases)
        }
    }
}

changelog {
    groups.empty()
    repositoryUrl = providers.gradleProperty("pluginRepositoryUrl")
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    publishPlugin {
        dependsOn(patchChangelog)
    }

}
