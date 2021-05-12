package org.xpathqs.fake

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.cache.ICache

class FakeCache : ICache {
    override fun setXml(xml: String, clear: Boolean) {
        
    }

    override fun isVisible(selector: ISelector): Boolean {
        return false
    }

    override fun isVisible(selectorXpath: String): Boolean {
        return false
    }

    override fun getElementsCount(selectorXpath: String): Int {
        return 0
    }

    override fun getAttribute(selectorXpath: String, attr: String, default: String): String? {
        return ""
    }

    override fun getAttributes(selectorXpath: String, attr: String): List<String> {
        return emptyList()
    }

    override fun clear(clearXml: Boolean) {
        
    }
}