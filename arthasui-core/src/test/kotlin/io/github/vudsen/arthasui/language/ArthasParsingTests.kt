package io.github.vudsen.arthasui.language

import com.intellij.testFramework.ParsingTestCase;
import io.github.vudsen.arthasui.language.arthas.ArthasParserDefinition

class ArthasParsingTests : ParsingTestCase("", "arthas", ArthasParserDefinition()) {

    fun testJfr() {
        doTest(true)
    }

    fun testLogger() {
        doTest(true)
    }

    fun testMc() {
        doTest(true)
    }

    fun testOptions() {
        doTest(true)
    }

    fun testSc() {
        doTest(true)
    }

    fun testSm() {
        doTest(true)
    }

    fun testStack() {
        doTest(true)
    }

    fun testTrace() {
        doTest(true)
    }

    fun testTt() {
        doTest(true)
    }

    override fun getTestDataPath(): String {
        return "src/test/testData"
    }

    override fun includeRanges(): Boolean {
        return true
    }
}