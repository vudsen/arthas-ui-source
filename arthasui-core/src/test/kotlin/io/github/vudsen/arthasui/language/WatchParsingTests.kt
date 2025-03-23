package io.github.vudsen.arthasui.language

import com.intellij.testFramework.ParsingTestCase;
import io.github.vudsen.arthasui.language.arthas.ArthasParserDefinition

class WatchParsingTests : ParsingTestCase("", "arthas", ArthasParserDefinition()) {

    fun testBasicUsage() {
        doTest(true)
    }

    override fun getTestDataPath(): String {
        return "src/test/testData"
    }

    override fun includeRanges(): Boolean {
        return true
    }
}