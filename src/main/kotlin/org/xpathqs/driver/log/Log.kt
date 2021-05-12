package org.xpathqs.driver.log

import org.xpathqs.driver.actions.IAction

object Log : ILog {
    private lateinit var logger: ILog

    fun init(logger: ILog) {
        Log.logger = logger
    }

    override fun log(msg: String) {
        logger.log(msg)
    }

    override fun error(msg: String) {
        logger.error(msg)
    }

    override fun <T> action(msg: String, lambda: () -> T): T {
        return logger.action(msg, lambda)
    }

    override fun action(action: IAction) {
        logger.action(action)
    }
}