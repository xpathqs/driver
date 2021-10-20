package org.xpathqs.driver.executor

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.IDriver
import org.xpathqs.driver.actions.IAction
import org.xpathqs.driver.actions.SelectorInteractionAction
import org.xpathqs.driver.actions.WaitAction
import org.xpathqs.driver.actions.WaitForSelectorAction
import org.xpathqs.driver.constants.Global
import org.xpathqs.driver.constants.Messages
import org.xpathqs.driver.exceptions.XPathQsException
import org.xpathqs.driver.log.Log
import org.xpathqs.log.style.StyledString

open class Executor(
    override val driver: IDriver
) : IExecutor {

    init {
        Messages.init()
    }

    override val actions: ActionExecMap = ActionExecMap().apply {
        set(WaitAction().name) {
            executeAction(it as WaitAction)
        }
    }

    override fun execute(action: IAction) {
        beforeAction(action)
        executeConcreteAction(action)
        afterAction(action)
    }

    override fun getElementsCount(selector: ISelector): Int {
        return 0
    }

    protected open fun executeConcreteAction(action: IAction) {
        Log.action(action) {
            if (hasActionHandler(action)) {
                getActionHandler(action).invoke(action)
            } else {
                throw XPathQsException.ActionNotFound(action, this)
            }
        }
    }

    override fun isPresent(selector: ISelector): Boolean {
        return false
    }

    override fun getAttr(selector: BaseSelector, attr: String): String {
        return ""
    }

    override fun getAttrs(selector: BaseSelector, attr: String): Collection<String> {
        return emptyList()
    }

    override fun hasActionHandler(action: IAction) = actions.containsKey(action.name)

    override fun getActionHandler(action: IAction) =
        actions[action.name] ?: throw  XPathQsException.ActionNotFound(action, this)

    override fun beforeAction(action: IAction) {
        if(action is SelectorInteractionAction) {
            Log.action(
                StyledString.fromDefaultFormatString(
                    Messages.Executor.beforeAction,
                    action.on.name
                ), "debug"
            ) {
                Global.executor.execute(
                    WaitForSelectorAction(action.on)
                )
                Log.xpath(action.on)
            }
        }
    }

    protected open fun executeAction(action: WaitAction) {
        Thread.sleep(action.timeout.toMillis())
    }
}