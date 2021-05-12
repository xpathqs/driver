package org.xpathqs.driver.executor

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.actions.IAction

interface IExecutor {
    fun execute(action: IAction)
    fun isPresent(selector: ISelector): Boolean

    fun getAttr(selector: BaseSelector, attr: String): String
    fun getAttrs(selector: BaseSelector, attr: String): Collection<String>

    fun beforeAction(action: IAction) {}
    fun afterAction(action: IAction) {}
}