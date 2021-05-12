package org.xpathqs.fake

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.cache.ICache

class FakeCache : ICache {
    override fun setXml(xml: String, clear: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isVisible(selector: ISelector): Boolean {
        TODO("Not yet implemented")
    }

    override fun isVisible(selectorXpath: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getElementsCount(selectorXpath: String): Int {
        TODO("Not yet implemented")
    }

    override fun getAttribute(selectorXpath: String, attr: String, default: String): String? {
        TODO("Not yet implemented")
    }

    override fun getAttributes(selectorXpath: String, attr: String): List<String> {
        TODO("Not yet implemented")
    }

    override fun clear(clearXml: Boolean) {
        TODO("Not yet implemented")
    }
}