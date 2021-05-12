package org.xpathqs.fake

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.cache.ICache

class FakeCache : ICache {
    override fun setXml(xml: String, clear: Boolean) {
        
    }

    override fun isVisible(selector: ISelector): Boolean {
        
    }

    override fun isVisible(selectorXpath: String): Boolean {
        
    }

    override fun getElementsCount(selectorXpath: String): Int {
        
    }

    override fun getAttribute(selectorXpath: String, attr: String, default: String): String? {
        
    }

    override fun getAttributes(selectorXpath: String, attr: String): List<String> {
        
    }

    override fun clear(clearXml: Boolean) {
        
    }
}