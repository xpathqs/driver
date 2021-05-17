package org.xpathqs.driver.log

import org.xpathqs.driver.actions.IAction

class ConsoleLog : ILog {
    override fun log(msg: String) {
        println(msg)
    }

    override fun error(msg: String) {
        println("error: $msg")
    }

    override fun <T> action(msg: String, lambda: () -> T): T {
        println("execute action $msg")
        return lambda()
    }

    override fun action(action: IAction) {
        println(action.toString())
    }
}