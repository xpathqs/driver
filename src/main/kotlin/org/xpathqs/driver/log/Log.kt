package org.xpathqs.driver.log

import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.actions.IAction
import org.xpathqs.log.BaseLogger
import org.xpathqs.log.annotations.LoggerBridge
import org.xpathqs.log.style.StyleFactory
import org.xpathqs.log.style.StyledBlock
import org.xpathqs.log.style.StyledString

@LoggerBridge
object Log {
    var log = BaseLogger()

    fun debug(msg: String) = debug(StyledBlock(msg))
    fun debug(msg: StyledBlock) = debug(StyledString(msg))
    fun debug(msg: StyledString) {
        log.debug(msg)
    }

    fun trace(msg: String) = trace(StyledBlock(msg))
    fun trace(msg: StyledBlock) = trace(StyledString(msg))
    fun trace(msg: StyledString) {
        log.trace(msg)
    }

    fun xpath(sel: ISelector) {
        if(sel.name.isNotEmpty()) {
            log.trace(StyleFactory.text( "xpath: ") + StyleFactory.xpath(sel.xpath))
        }
    }

    fun info(msg: String) = info(StyledBlock(msg))
    fun info(msg: StyledBlock) = info(StyledString(msg))
    fun info(msg: StyledString) {
        log.info(msg)
    }

    fun always(msg: String) = always(StyledBlock(msg))
    fun always(msg: StyledBlock) = always(StyledString(msg))
    fun always(msg: StyledString) {
        log.always(msg)
    }

    fun error(msg: String) = error(StyledBlock(msg))
    fun error(msg: StyledBlock) = error(StyledString(msg))
    fun error(msg: StyledString) {
        log.error(msg)
    }

    fun addAttachment(value: String, type: String, data: Any) {
        log.addAttachment(value, type, data)
    }

    fun <T> action(msg: String, tag: String = "action", lambda: () -> T) = action(StyledBlock(msg), tag, lambda)
    fun <T> action(msg: StyledBlock, tag: String = "action", lambda: () -> T) = action(StyledString(msg), tag, lambda)
    fun <T> action(msg: StyledString, tag: String = "action", lambda: () -> T): T {
        return log.action(msg, tag, lambda = lambda)
    }

    fun <T> action(action: IAction, tag: String = "action", lambda: () -> T): T {
        return action(action.toStyledString(), tag, lambda = lambda)
    }
}