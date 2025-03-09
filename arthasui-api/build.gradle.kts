dependencies {
    testImplementation(kotlin("test"))
}
group = "io.github.vudsen.arthasui.api"
tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}