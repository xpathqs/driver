package org.xpathqs.driver.log

import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.actions.IAction
import org.xpathqs.log.TcLogger
import org.xpathqs.log.annotations.LoggerBridge

@LoggerBridge
object Log {
    var log = TcLogger()

    fun debug(msg: String) {
        log.debug(msg)
    }

    fun trace(msg: String) {
        log.trace(msg)
    }

    fun xpath(sel: ISelector) {
        log.trace("xpath: " + sel.toXpath())
    }

    fun info(msg: String) {
        log.info(msg)
    }

    fun always(msg: String) {
        log.always(msg)
    }

    fun error(msg: String) {
        log.error(msg)
    }

    fun <T> action(msg: String, tag: String = "action", lambda: () -> T): T {
        return log.action(msg, tag, lambda = lambda)
    }

    fun <T> action(action: IAction, tag: String = "action", lambda: () -> T): T {
        return log.action(action.toString(), tag, lambda = lambda)
    }
}