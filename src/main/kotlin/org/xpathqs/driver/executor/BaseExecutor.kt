package org.xpathqs.driver.executor

import org.xpathqs.driver.IDriver
import org.xpathqs.driver.actions.IAction
import org.xpathqs.driver.actions.WaitAction
import org.xpathqs.driver.exceptions.XPathQsException
import org.xpathqs.driver.log.Log

typealias ActionExecLambda = (IAction) -> Unit
typealias ActionExecMap = HashMap<String, ActionExecLambda>

abstract class BaseExecutor(
    protected open val driver: IDriver
) : IExecutor {

    private val actionHandlerBase = ActionExecMap().apply {
        set(WaitAction().name) {
            executeAction(it as WaitAction)
        }
    }

    override fun execute(action: IAction) {
        beforeAction(action)
        executeConcreteAction(action)
        afterAction(action)
    }

    open fun hasActionHandler(action: IAction) = actionHandlerBase.containsKey(action.name)

    open fun getActionHandler(action: IAction) =
        actionHandlerBase[action.name] ?: throw  XPathQsException.ActionNotFound(action, this)

    protected open fun executeConcreteAction(action: IAction) {
        if (hasActionHandler(action)) {
            Log.action(action)
            getActionHandler(action).invoke(action)
        } else {
            throw XPathQsException.ActionNotFound(action, this)
        }
    }

    private fun executeAction(action: WaitAction) {
        Thread.sleep(action.timeout.toMillis())
    }
}