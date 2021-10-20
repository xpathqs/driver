package org.xpathqs.driver.page.determinationparser

import org.junit.jupiter.api.Test

import org.xpathqs.core.reflection.scanPackage
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.util.SelectorFactory.tagSelector
import org.xpathqs.driver.navigation.annotations.DeterminationType
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.page.MockNavDetermination
import org.xpathqs.driver.navigation.util.Determination
import org.xpathqs.driver.navigation.util.DeterminationParser
import org.xpathqs.gwt.WHEN

object Determination1: MockNavDetermination() {
    @UI.Nav.DeterminateBy(DeterminationType.EXIST)
    val s1 = tagSelector("div")
}

object Determination2: MockNavDetermination() {
    @UI.Nav.DeterminateBy(DeterminationType.EXIST)
    val s1 = tagSelector("div")

    @UI.Nav.DeterminateBy(DeterminationType.EXIST)
    val s2 = tagSelector("div")

    val s3 = tagSelector("div")
}

object Determination3: MockNavDetermination() {
    val s1 = tagSelector("div")

    @UI.Nav.DeterminateBy(DeterminationType.EXIST)
    object Inner: Block() {
        val s1 = tagSelector("div")
        val s2 = tagSelector("div")
    }
}

object Determination4: MockNavDetermination() {
    val s1 = tagSelector("div")

    @UI.Nav.DeterminateBy(DeterminationType.EXIST_ALL)
    object Inner: Block(tagSelector("d")) {
        val s1 = tagSelector("div")
        val s2 = tagSelector("div")
    }
}

object Determination5: MockNavDetermination() {
    val s1 = tagSelector("div")

    object Inner: Block(tagSelector("d")) {
        val s1 = tagSelector("div")
        val s2 = tagSelector("div")
        @UI.Nav.DeterminateBy
        object Inner2: Block(tagSelector("d")) {
            val s1 = tagSelector("div")
            val s2 = tagSelector("div")

        }
    }
}
internal class DeterminationParserTest {

    init {
        scanPackage(this)
    }

    @Test
    fun parse_page3() {
        WHEN {
            DeterminationParser(Determination3)
                .parse()
        }.THEN {
            Determination()
        }
    }

    @Test
    fun parse_page5() {
        WHEN {
            DeterminationParser(Determination5)
                .parse()
        }.THEN {
            Determination(
                exist = Determination5.Inner.Inner2
            )
        }
    }
}