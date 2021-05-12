package org.xpathqs.driver.log

import org.xpathqs.driver.actions.IAction

interface ILog {
    fun log(msg: String)
    fun error(msg: String)
    fun <T> action(msg: String, lambda: () -> T): T
    fun action(action: IAction)
}