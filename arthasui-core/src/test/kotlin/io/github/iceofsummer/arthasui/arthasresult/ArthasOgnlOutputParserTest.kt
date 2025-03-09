package io.github.vudsen.arthasui.arthasresult;

import io.github.vudsen.arthasui.common.lang.ArthasOgnlOutputParserV2
import io.github.vudsen.arthasui.common.lang.model.ArthasArray
import io.github.vudsen.arthasui.common.lang.model.ArthasMap
import io.github.vudsen.arthasui.common.lang.model.ArthasObject
import io.github.vudsen.arthasui.common.lang.model.ArthasValue
import io.github.vudsen.arthasui.api.ArthasResultItem
import org.junit.Test;

import org.junit.Assert.*;

class ArthasOgnlOutputParserTest {

    @Test
    fun doParse_ambiguousOutput() {
        val text = """@HashMap[
            @String[[]\[@ArrayList]:@ArrayList[
                @String[Hello World],
            ],
        ]"""

        val result = ArthasOgnlOutputParserV2(text).doParse();
        println(result)

    }

    @Test
    fun doParse_parseArrayWithUncommonChars() {
        val text = "@ArrayList[\n@String[D:\\DevelopmentTool\\xxx-xxx\\app\\java],\n@String[Java(TM) SE Runtime Environment],\n]"
        val result = ArthasOgnlOutputParserV2.parse(text);
        val values = mutableListOf<ArthasResultItem>()
        values.add(ArthasValue("D:\\DevelopmentTool\\xxx-xxx\\app\\java", "String"))
        values.add(ArthasValue("Java(TM) SE Runtime Environment", "String"))
        assertEquals(ArthasArray(values, "ArrayList"), result)
    }

    @Test
    fun doParse_emptyArray() {
        val text = """@ArrayList[
        ]""".trimMargin()
        val result = ArthasOgnlOutputParserV2.parse(text);

        assertEquals(ArthasArray(mutableListOf(), "ArrayList"), result)
    }

    @Test
    fun doParse_parseArthasObject() {
        val text = """@Test[
            value=@Integer[2],
            str=@String[test],
        ] 
        """;
        val result = ArthasOgnlOutputParserV2.parse(text);

        val fields = linkedMapOf<String, ArthasResultItem>()
        fields["value"] = ArthasValue("2", "Integer")
        fields["str"] = ArthasValue("test", "String")

        assertEquals(ArthasObject(fields, "Test"), result)
    }

    @Test
    fun doParse_parseArthasArray() {
        val text = """@SingletonSet[
            @String[test3],
            @Integer[222],
        ]
        """;
        val result = ArthasOgnlOutputParserV2.parse(text);
        val values = mutableListOf<ArthasResultItem>()
        values.add(ArthasValue("test3", "String"))
        values.add(ArthasValue("222", "Integer"))

        assertEquals(ArthasArray(values, "SingletonSet"), result)
    }

    @Test
    fun doParse_parseArthasMap() {
        val text = """@HashMap[
            @String[test]:@String[test],
            @String[test2]:@Integer[1],
        ]"""
        val result = ArthasOgnlOutputParserV2.parse(text);

        assertEquals(ArthasMap(linkedMapOf(), "HashMap").apply {
            entries[ArthasValue("test", "String")] = ArthasValue("test", "String")
            entries[ArthasValue("test2", "String")] = ArthasValue("1", "Integer")
        }, result)
    }

    @Test
    fun doParse_parseComplexExpression() {
        val text = """@HashMap[
            @String[test2]:@Integer[1],
            @String[test3]:@SingletonSet[
                @String[test3],
            ],
            @String[test]:@String[test],
        ]
        """
        val result = ArthasOgnlOutputParserV2.parse(text);

        val expected = ArthasMap(linkedMapOf(), "HashMap").apply {
            entries[ArthasValue("test2", "String")] = ArthasValue("1", "Integer")
            entries[ArthasValue("test3", "String")] = ArthasArray(mutableListOf(ArthasValue("test3", "String")), "SingletonSet")
            entries[ArthasValue("test", "String")] = ArthasValue("test", "String")
        }
        assertEquals(expected, result)
    }
}