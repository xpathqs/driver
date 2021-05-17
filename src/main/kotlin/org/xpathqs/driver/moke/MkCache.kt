package org.xpathqs.driver.moke

import org.xpathqs.driver.cache.ICache

open class MkCache : ICache {
    override fun update(xml: String) {

    }

    override fun isPresent(xpath: String): Boolean {
        return false
    }

    override fun getElementsCount(xpath: String): Int {
        return 0
    }

    override fun getAttribute(selectorXpath: String, name: String): String {
        return ""
    }

    override fun getAttributes(selectorXpath: String, name: String): List<String> {
        return emptyList()
    }

    override fun clear() {

    }
}