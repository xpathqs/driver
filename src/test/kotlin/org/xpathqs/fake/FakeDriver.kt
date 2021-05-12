package org.xpathqs.fake

import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.IDriver

class FakeDriver : IDriver {

    override fun click(x: Int, y: Int) {
        TODO("Not yet implemented")
    }

    override fun click(sel: ISelector) {
        TODO("Not yet implemented")
    }

    override fun clear(xpath: ISelector) {
        TODO("Not yet implemented")
    }

    override fun input(sel: ISelector, value: String) {
        TODO("Not yet implemented")
    }

    override val pageSource: String
        get() = ""
}