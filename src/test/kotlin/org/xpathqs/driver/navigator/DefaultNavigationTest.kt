package org.xpathqs.driver.navigator

import org.junit.jupiter.api.Test
import org.xpathqs.core.reflection.PackageScanner
import org.xpathqs.core.util.SelectorFactory.tagSelector
import org.xpathqs.driver.extensions.click
import org.xpathqs.driver.navigation.Navigator
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.page.NavDetermination
import org.xpathqs.gwt.WHEN

val navigator = Navigator()

object Determination1: NavDetermination(navigator = navigator) {
    val button = tagSelector("div")

    override fun initNavigation() {
        addNavigation(Determination2) {
            button.click()
        }
    }
}

object Determination2: NavDetermination(navigator = navigator) {
    val button = tagSelector("div")

    override fun initNavigation() {
        addNavigation(Determination3) {
            button.click()
        }
    }
}

object Determination3: NavDetermination(navigator = navigator) {
    @UI.Nav.DeterminateBy
    val button = tagSelector("div")

    override fun initNavigation() {
        addNavigation(Determination1) {
            button.click()
        }
    }
}

class DefaultNavigationTest {

    init {
        PackageScanner(this::class.java.`package`.name)
            .scan()
        navigator.initNavigations()
    }

    @Test
    fun test1() {
        WHEN {
            navigator.findPath(Determination2, Determination1).toString()
        }.THEN {
            "[(Determination2 : Determination3), (Determination3 : Determination1)]"
        }
    }

    @Test
    fun test2() {
        WHEN {
            navigator.findPath(Determination1, Determination3).toString()
        }.THEN {
            "[(Determination1 : Determination2), (Determination2 : Determination3)]"
        }
    }

    @Test
    fun test4() {
        WHEN {
            navigator.findPath(Determination1, Determination1).toString()
        }.THEN {
            "[Determination1]"
        }
    }

    @Test
    fun test3() {
        WHEN {
            navigator.findPath(Determination3, Determination1).toString()
        }.THEN {
            "[(Determination3 : Determination1)]"
        }
    }

    @Test
    fun testDetermination() {
        WHEN {
            Determination3.determination.exist
        }.THEN {
            arrayListOf(Determination3.button)
        }
    }
}