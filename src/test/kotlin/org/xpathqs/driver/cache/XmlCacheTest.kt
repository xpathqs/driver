package org.xpathqs.driver.cache

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class XmlCacheTest {

    fun getResourceAsText(path: String): String {
        return this::class.java.classLoader.getResource(path).readText()
    }

    @Test
    fun update() {
        val xml = getResourceAsText("pagesource/ios/app1.xml")
        val cache = XmlCache()

        cache.update(xml)

        val xpath = "//*[@name='Home, tab, 1 of 3 Settings, tab, 2 of 3 Claims, tab, 3 of 3']"

        assertThat(
            cache.getElementsCount(xpath)
        ).isEqualTo(2)
    }
}