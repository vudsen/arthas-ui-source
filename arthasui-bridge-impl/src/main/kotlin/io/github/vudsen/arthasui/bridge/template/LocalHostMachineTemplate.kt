package io.github.vudsen.arthasui.bridge.template

import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.template.HostMachineTemplate
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.File

/**
 * 针对本地宿主机的实现
 *
 * 由于服务器基本都是 Linux，所以没有必要再给 Windows 和 MacOS 上维护代码了。对于这俩强制要求只能连接本地的 JVM，不提供远程连接的实现。
 */
class LocalHostMachineTemplate(private val hostMachine: HostMachine) : HostMachineTemplate {

    override fun isArm(): Boolean {
        return false
    }

    override fun isFileNotExist(path: String): Boolean {
        return File(path).exists()
    }

    override fun mkdirs(path: String) {
        File(path).mkdirs()
    }

    override fun download(url: String, destDir: String) {
        val destFile = File(destDir, url.substringAfterLast("/"))
        destFile.outputStream().use { output ->
            java.net.URL(url).openStream().use { input ->
                input.copyTo(output)
            }
        }
    }

    override fun unzip(target: String) {
        val file = File(target)
        val destDir = file.parentFile
        if (file.extension == "zip") {
            java.util.zip.ZipFile(file).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    val entryDest = File(destDir, entry.name)
                    if (entry.isDirectory) {
                        entryDest.mkdirs()
                    } else {
                        entryDest.outputStream().use { output ->
                            zip.getInputStream(entry).use { input ->
                                input.copyTo(output)
                            }
                        }
                    }
                }
            }
        } else if (file.extension == "tgz") {
            java.util.zip.GZIPInputStream(file.inputStream()).use { gzipInput ->
                TarArchiveInputStream(gzipInput).use { tarInput ->
                    var entry = tarInput.nextTarEntry
                    while (entry != null) {
                        val entryDest = File(destDir, entry.name)
                        if (entry.isDirectory) {
                            entryDest.mkdirs()
                        } else {
                            entryDest.outputStream().use { output ->
                                tarInput.copyTo(output)
                            }
                        }
                        entry = tarInput.nextTarEntry
                    }
                }
            }
        }
    }

    override fun grep(source: String, search: String): String {
        // 本地就不考虑网络占用了
        val ok = hostMachine.execute(source).ok()
        return ok.split('\n').filter { it.contains(search) }.joinToString("\n")
    }

    override fun env(name: String): String? {
        return System.getenv(name)
    }

    override fun test(): Boolean {
        return true
    }

    override fun getHostMachine(): HostMachine {
        return hostMachine
    }


}