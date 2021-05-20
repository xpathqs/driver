package org.xpathqs.driver.executor

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.IDriver
import org.xpathqs.driver.actions.IAction
import org.xpathqs.driver.exceptions.XPathQsException
import org.xpathqs.driver.log.Log

open class Decorator(
    protected val origin: IExecutor
) : IExecutor {

    override fun execute(action: IAction) {
        if (hasActionHandler(action)) {
            beforeAction(action)
            executeConcreteAction(action)
            afterAction(action)
        } else {
            origin.execute(action)
        }
    }

    override fun isPresent(selector: ISelector): Boolean {
        return origin.isPresent(selector)
    }

    override fun getAttr(selector: BaseSelector, attr: String): String {
        return origin.getAttr(selector, attr)
    }

    override fun getAttrs(selector: BaseSelector, attr: String): Collection<String> {
        return origin.getAttrs(selector, attr)
    }

    override fun hasActionHandler(action: IAction): Boolean {
        if (actions.containsKey(action.name)) {
            return true
        }
        return origin.hasActionHandler(action)
    }

    override fun beforeAction(action: IAction) {
        origin.beforeAction(action)
    }

    override fun getActionHandler(action: IAction): ActionExecLambda {
        return actions[action.name] ?: origin.getActionHandler(action)
    }

    override val driver: IDriver
        get() = origin.driver

    override val actions: ActionExecMap
        get() = origin.actions

    protected open fun executeConcreteAction(action: IAction) {
        if (hasActionHandler(action)) {
            Log.action(action)
            getActionHandler(action).invoke(action)
        } else {
            throw XPathQsException.ActionNotFound(action, this)
        }
    }
}