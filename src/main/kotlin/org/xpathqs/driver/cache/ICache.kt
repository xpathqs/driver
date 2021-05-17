package org.xpathqs.driver.cache

interface ICache {
    fun update(xml: String)

    fun isPresent(xpath: String): Boolean
    fun getElementsCount(xpath: String): Int

    fun getAttribute(xpath: String, name: String): String
    fun getAttributes(xpath: String, name: String): Collection<String>

    fun clear() {}
}