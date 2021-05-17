package org.xpathqs.driver.executor

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.IDriver
import org.xpathqs.driver.actions.IAction

typealias ActionExecLambda = (IAction) -> Unit
typealias ActionExecMap = HashMap<String, ActionExecLambda>

interface IExecutor {
    fun execute(action: IAction)
    fun isPresent(selector: ISelector): Boolean

    fun getAttr(selector: BaseSelector, attr: String): String
    fun getAttrs(selector: BaseSelector, attr: String): Collection<String>

    fun beforeAction(action: IAction) {}
    fun afterAction(action: IAction) {}

    fun hasActionHandler(action: IAction): Boolean
    fun getActionHandler(action: IAction): ActionExecLambda

    val driver: IDriver
    val actions: ActionExecMap
}