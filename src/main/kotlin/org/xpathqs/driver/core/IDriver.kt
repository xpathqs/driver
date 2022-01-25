package org.xpathqs.driver.core

import org.xpathqs.core.selector.base.ISelector

interface IDriver {
    fun click(x: Int, y: Int)

    fun click(selector: ISelector)
    fun clear(selector: ISelector)
    fun input(selector: ISelector, value: String)

    val pageSource: String
}