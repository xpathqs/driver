package org.xpathqs.driver.mokexml

import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.IDriver

class MockDriver(private val source: String): IDriver {
    override val pageSource: String
        get() = source

    override fun clear(selector: ISelector) {}
    override fun click(x: Int, y: Int) {}
    override fun click(selector: ISelector) {}
    override fun input(selector: ISelector, value: String) {}
}