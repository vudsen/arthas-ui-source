dependencies {
    testImplementation(kotlin("test"))
    api(project(":arthasui-api"))
}
group = "io.github.vudsen.arthasui.common"
tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}