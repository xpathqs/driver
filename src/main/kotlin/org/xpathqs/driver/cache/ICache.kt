package org.xpathqs.driver.cache

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector

interface ICache {
    fun setXml(xml: String, clear: Boolean = true)
    fun isVisible(selector: ISelector): Boolean
    fun isVisible(selectorXpath: String): Boolean
    fun getElementsCount(selectorXpath: String): Int
    fun getAttribute(selectorXpath: String, attr: String, default: String = ""): String?
    fun getAttributes(selectorXpath: String, attr: String): List<String>
    fun clear(clearXml: Boolean = true)
}