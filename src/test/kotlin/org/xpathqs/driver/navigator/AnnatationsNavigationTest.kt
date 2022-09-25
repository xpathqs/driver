package org.xpathqs.driver.navigator

import org.junit.jupiter.api.Test
import org.xpathqs.core.reflection.PackageScanner
import org.xpathqs.core.util.SelectorFactory.tagSelector
import org.xpathqs.driver.navigation.annotations.UI

import org.xpathqs.driver.page.NavDetermination
import org.xpathqs.gwt.WHEN

@OptIn(ExperimentalStdlibApi::class)
object AnnDetermination1: NavDetermination(navigator = navigator) {
    @UI.Nav.PathTo(byClick = AnnDetermination2::class)
    val button = tagSelector("div")
}

@OptIn(ExperimentalStdlibApi::class)
object AnnDetermination2: NavDetermination(navigator = navigator) {
    @UI.Nav.PathTo(byClick = AnnDetermination3::class)
    val button = tagSelector("div")
}

@OptIn(ExperimentalStdlibApi::class)
object AnnDetermination3: NavDetermination(navigator = navigator) {
    @UI.Nav.PathTo(byClick = AnnDetermination1::class)
    @UI.Nav.DeterminateBy()
    val button = tagSelector("div")
}

class AnnotationsNavigationTest {

    init {
        PackageScanner(this::class.java.`package`.name)
            .scan()
        navigator.initNavigations()
    }
/*

    @Test
    fun test1() {
        WHEN {
            navigator.findPath(AnnDetermination2, AnnDetermination1).toString()
        }.THEN {
            "[(AnnDetermination2 : AnnDetermination3), (AnnDetermination3 : AnnDetermination1)]"
        }
    }

    @Test
    fun test2() {
        WHEN {
            navigator.findPath(AnnDetermination1, AnnDetermination3).toString()
        }.THEN {
            "[(AnnDetermination1 : AnnDetermination2), (AnnDetermination2 : AnnDetermination3)]"
        }
    }

    @Test
    fun test4() {
        WHEN {
            navigator.findPath(AnnDetermination1, AnnDetermination1).toString()
        }.THEN {
            "[AnnDetermination1]"
        }
    }

    @Test
    fun test3() {
        WHEN {
            navigator.findPath(AnnDetermination3, AnnDetermination1).toString()
        }.THEN {
            "[(AnnDetermination3 : AnnDetermination1)]"
        }
    }
*/

    @Test
    fun testDetermination() {
        WHEN {
            AnnDetermination3.determination.exist
        }.THEN {
            arrayListOf(AnnDetermination3.button)
        }
    }
}