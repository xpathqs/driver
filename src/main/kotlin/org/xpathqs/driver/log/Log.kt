package org.xpathqs.driver.log

import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.actions.IAction
import org.xpathqs.log.Log

fun Log.xpath(sel: ISelector) = Log.xpath(sel.xpath)

fun <T> Log.action(action: IAction, tag: String = "action", lambda: () -> T): T {
    return Log.action(action.toStyledString(), tag, lambda = lambda)
}