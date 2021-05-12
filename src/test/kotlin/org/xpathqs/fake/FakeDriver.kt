package org.xpathqs.fake

import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.IDriver

class FakeDriver : IDriver {

    override fun click(x: Int, y: Int) {
        
    }

    override fun click(sel: ISelector) {
        
    }

    override fun clear(xpath: ISelector) {
        
    }

    override fun input(sel: ISelector, value: String) {
        
    }

    override val pageSource: String
        get() = ""
}